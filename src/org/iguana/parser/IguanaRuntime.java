package org.iguana.parser;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.*;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.gss.*;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.ParserResultOps;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.Configuration;
import org.iguana.util.ParserLogger;
import org.iguana.util.Tuple;
import org.iguana.utils.input.Input;

import java.util.*;
import java.util.function.Function;

public class IguanaRuntime<T extends Result> {

    private final Deque<Descriptor<T>> descriptorPool;

    private final Deque<Descriptor<T>> descriptorsStack;

    private final IEvaluatorContext ctx;

    private final Configuration config;

    private final ParserLogger logger = ParserLogger.getInstance();

    private final ResultOps<T> resultOps;

    // A priority queue (max heap) containing the parse errors thrown the parsing, sorted by the input index.
    // The top of the priority queue is the parse error thrown at the last input position.
    private final PriorityQueue<ParseError<T>> parseErrors;

    private StartGSSNode<T> startGSSNode;

    public IguanaRuntime(Configuration config, ResultOps<T> resultOps) {
        this.config = config;
        this.resultOps = resultOps;
        this.descriptorsStack = new ArrayDeque<>(512);
        this.descriptorPool = new ArrayDeque<>(512);
        this.ctx = GLLEvaluator.getEvaluatorContext(config);
        this.parseErrors = new PriorityQueue<>((error1, error2) -> error2.getInputIndex() - error1.getInputIndex());
    }

    public T run(Input input, Nonterminal start, GrammarGraph grammarGraph, Map<String, Object> map, boolean global) {
        clearState(grammarGraph);

        IEvaluatorContext ctx = getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        Environment env = ctx.getEmptyEnvironment();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            env = env._declare(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Expression> entry : grammarGraph.getGlobals().entrySet()) {
            env = env._declare(entry.getKey(), entry.getValue().interpret(ctx, input));
        }

        NonterminalGrammarSlot startSlot = grammarGraph.getStartSlot(start);
        List<String> parameters = startSlot.getParameters();
        // TODO: Make parameters an empty list by default
        if (parameters != null && !parameters.isEmpty()) {
            if (!global && !env.isEmpty()) {
                Object[] arguments = new Object[parameters.size()];

                int i = 0;
                for (String parameter : startSlot.getParameters()) {
                    arguments[i++] = env.lookup(parameter);
                }

                startGSSNode = new StartGSSNode<>(startSlot, 0, arguments);
                env = env.declare(startSlot.getParameters().toArray(new String[0]), arguments);
            } else {
                startSlot = grammarGraph.getStartSlot(Nonterminal.withName("$_" + start.getName()));
                if (startSlot == null) {
                    throw new RuntimeException(
                            "No top level definition exists for " + start.getName() + " " + parameters);
                }
                startGSSNode = new StartGSSNode<>(startSlot, 0);
            }
        } else {
            startGSSNode = new StartGSSNode<>(startSlot, 0);
        }

        ParserLogger logger = ParserLogger.getInstance();
        logger.reset();

        for (BodyGrammarSlot slot : startSlot.getFirstSlots()) {
            scheduleDescriptor(slot, startGSSNode, getResultOps().dummy(), env);
        }

        return runParserLoop(startGSSNode, input);
    }

    public T runParserLoop(StartGSSNode<T> startGSSNode, Input input) {
        while (hasDescriptor()) {
            Descriptor<T> descriptor = nextDescriptor();
            logger.processDescriptor(descriptor);
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(),
                    descriptor.getEnv(), this);
        }

        int inputLength = input.length() - 1;
        T result = startGSSNode.getResult(inputLength);
        // If there was a successful parse (covering the whole input range) for the start symbol
        if (result != null && result.getRightExtent() == inputLength) {
            return result;
        }
        return null;
    }

    private void clearState(GrammarGraph grammarGraph) {
        grammarGraph.clear();
        descriptorPool.clear();
        descriptorsStack.clear();
        parseErrors.clear();
    }

    public void recordParseError(
            int inputIndex,
            Input input,
            GrammarSlot slot,
            GSSNode<T> gssNode,
            String description) {
        ParseError<T> error = new ParseError<>(slot, gssNode, inputIndex, input.getLineNumber(inputIndex),
                input.getColumnNumber(inputIndex), description);
        parseErrors.add(error);
        logger.error(error);
    }

    /*
     * Tries to recover the error from an error edge, i.e., of the form X = alpha . Error beta
     */
    public void recoverFromError(GSSEdge<T> edge, ErrorTransition errorTransition, Input input) {
        T result = edge instanceof DummyGSSEdge<?> ? resultOps.dummy() : edge.getResult();
        Environment env = edge.getEnv();
        errorTransition.handleError(input, edge.getDestination(), result, env, this);
    }

    // Collects all the GSS edges with the label of the form X = alpha . Error beta that are reachable
    // from the current GSS node to the start symbol GSS node.
    public void collectErrorSlots(
            GSSNode<T> gssNode,
            List<Tuple<GSSEdge<T>, ErrorTransition>> result,
            Set<GSSNode<T>> visited) {
        if (visited.contains(gssNode)) return;
        visited.add(gssNode);
        if (gssNode == null) return;
        for (GSSEdge<T> edge : gssNode.getGSSEdges()) {
            ErrorTransition errorTransition = getErrorTransition(edge);
            if (errorTransition != null) {
                result.add(Tuple.of(edge, errorTransition));
            }
            collectErrorSlots(edge.getDestination(), result, visited);
        }
    }

    private ErrorTransition getErrorTransition(GSSEdge<T> edge) {
        if (edge.getReturnSlot() == null) return null;
        // This covers the cases with no layout insertion: X alpha . Error
        if (edge.getReturnSlot().getOutTransition() instanceof ErrorTransition) {
            return (ErrorTransition) edge.getReturnSlot().getOutTransition();
        }
        // This covers cases where the layout is inserted.
        // X = alpha . Layout Error
        if (edge.getReturnSlot().getOutTransition() != null &&
            edge.getReturnSlot().getOutTransition().destination() != null &&
            edge.getReturnSlot().getOutTransition().destination().getOutTransition() instanceof ErrorTransition) {
            return (ErrorTransition) edge.getReturnSlot().getOutTransition().destination().getOutTransition();
        }
        return null;
    }

    public boolean hasDescriptor() {
        return !descriptorsStack.isEmpty();
    }

    public Descriptor<T> nextDescriptor() {
        Descriptor<T> descriptor = descriptorsStack.pop();
        descriptorPool.push(descriptor);
        return descriptor;
    }

    public void scheduleDescriptor(BodyGrammarSlot grammarSlot, GSSNode<T> gssNode, T result, Environment env) {
        Descriptor<T> descriptor;
        if (!descriptorPool.isEmpty()) {
            descriptor = descriptorPool.pop();
            descriptor.init(grammarSlot, gssNode, result, env);
        } else {
            descriptor = new Descriptor<>(grammarSlot, gssNode, result, env);
        }
        descriptorsStack.push(descriptor);
        logger.descriptorAdded(descriptor);
    }

    public IEvaluatorContext getEvaluatorContext() {
        return ctx;
    }

    public Environment getEnvironment() {
        return ctx.getEnvironment();
    }

    public void setEnvironment(Environment env) {
        ctx.setEnvironment(env);
    }

    public Environment getEmptyEnvironment() {
        return ctx.getEmptyEnvironment();
    }

    public GSSEdge<T> createGSSEdge(BodyGrammarSlot returnSlot, T result, GSSNode<T> gssNode, Environment env) {
        if (result.isDummy()) {
            if (env.isEmpty()) {
                return gssNode != null ? new DummyGSSEdge<>(returnSlot, gssNode) : new CyclicDummyGSSEdges<>();
            } else {
                return gssNode != null ? new DummyGSSEdgeWithEnv<>(returnSlot, gssNode, env)
                        : new CyclicDummyGSSEdgesWithEnv<>(env);
            }
        }

        if (env.isEmpty()) {
            return new DefaultGSSEdge<>(returnSlot, result, gssNode);
        } else {
            return new DefaultGSSEdgeWithEnv<>(returnSlot, result, gssNode, env);
        }
    }

    public void evaluate(Statement[] statements, Environment env, Input input) {
        assert statements.length > 0;

        ctx.setEnvironment(env);

        int i = 0;
        while (i < statements.length) {
            statements[i].interpret(ctx, input);
            i++;
        }
    }

    public Object evaluate(Expression expression, Environment env, Input input) {
        ctx.setEnvironment(env);
        return expression.interpret(ctx, input);
    }

    public Object[] evaluate(Expression[] arguments, Environment env, Input input) {
        if (arguments == null) return null;

        ctx.setEnvironment(env);

        Object[] values = new Object[arguments.length];

        int i = 0;
        while (i < arguments.length) {
            values[i] = arguments[i].interpret(ctx, input);
            i++;
        }

        return values;
    }

    public PriorityQueue<ParseError<T>> getParseErrors() {
        return new PriorityQueue<>(parseErrors);
    }

    public RecognizerStatistics getStatistics() {
        if (resultOps instanceof ParserResultOps) {
            return ParseStatistics.builder()
                    .setDescriptorsCount(logger.getDescriptorsCount())
                    .setGSSNodesCount(logger.getCountGSSNodes() + 1) // + start gss node
                    .setGSSEdgesCount(logger.getCountGSSEdges())
                    .setNonterminalNodesCount(logger.getCountNonterminalNodes())
                    .setTerminalNodesCount(logger.getCountTerminalNodes())
                    .setIntermediateNodesCount(logger.getCountIntermediateNodes())
                    .setPackedNodesCount(logger.getCountPackedNodes())
                    .setAmbiguousNodesCount(logger.getCountAmbiguousNodes())
                    .build();
        } else {
            return RecognizerStatistics.builder()
                    .setDescriptorsCount(logger.getDescriptorsCount())
                    .setGSSNodesCount(logger.getCountGSSNodes() + 1) // + start gss node
                    .setGSSEdgesCount(logger.getCountGSSEdges())
                    .build();
        }

    }

    public Configuration getConfiguration() {
        return config;
    }

    public int getDescriptorPoolSize() {
        return descriptorPool.size();
    }

    public ResultOps<T> getResultOps() {
        return resultOps;
    }

    public StartGSSNode<T> getStartGSSNode() {
        return startGSSNode;
    }

    private static void printStats(GrammarGraph grammarGraph) {
        for (TerminalGrammarSlot slot : grammarGraph.getTerminalGrammarSlots()) {
            System.out.println(slot.getTerminal().getName() + " : " + slot.countTerminalNodes());
        }

        for (NonterminalGrammarSlot slot : grammarGraph.getNonterminalGrammarSlots()) {
            System.out.print(slot.getNonterminal().getName());
            System.out.println(" GSS nodes: " + slot.countGSSNodes());
            double[] poppedElementStats = stats(slot.getGSSNodes(), GSSNode::countPoppedElements);
            double[] gssEdgesStats = stats(slot.getGSSNodes(), GSSNode::countGSSEdges);
            if (poppedElementStats == null)
                System.out.println("Popped Elements: empty");
            else
                System.out.printf("Popped Elements (min: %d, max: %d, mean: %.2f)%n", (int) poppedElementStats[0],
                        (int) poppedElementStats[1], poppedElementStats[2]);

            if (gssEdgesStats == null)
                System.out.println("GSS Edges: empty");
            else
                System.out.printf("GSS Edges (min: %d, max: %d, mean: %.2f)%n", (int) gssEdgesStats[0],
                        (int) gssEdgesStats[1], gssEdgesStats[2]);
            System.out.println("---------------");
        }
    }

    private static void printGSSInfo(GrammarGraph grammarGraph) {
        Comparator<GSSNode<?>> edgeComparator = (node1, node2) -> node2.countGSSEdges() - node1.countGSSEdges();
        List<GSSNode<?>> gssNodes = new ArrayList<>();
        for (NonterminalGrammarSlot slot : grammarGraph.getNonterminalGrammarSlots()) {
            for (GSSNode<?> gssNode : slot.getGSSNodes()) {
                gssNodes.add(gssNode);
            }
        }

        gssNodes.sort(edgeComparator);

        for (GSSNode<?> gssNode : gssNodes) {
            System.out.println(
                    gssNode + ", edges: " + gssNode.countGSSEdges() + ", poppedElements: " +
                    gssNode.countPoppedElements());
        }
    }

    private static double[] stats(Iterable<GSSNode<?>> gssNodes, Function<GSSNode<?>, Integer> f) {
        if (!gssNodes.iterator().hasNext()) return null;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int sum = 0;
        int count = 0;

        for (GSSNode<?> gssNode : gssNodes) {
            min = Integer.min(min, f.apply(gssNode));
            max = Integer.max(max, f.apply(gssNode));
            sum += f.apply(gssNode);
            count++;
        }

        return new double[]{min, max, (double) sum / count};
    }
}
