package org.iguana.parser;

import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.gss.*;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.ParserResultOps;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.Configuration;
import org.iguana.util.ParserLogger;

import java.util.*;
import java.util.function.Function;

public class IguanaRuntime<T extends Result> {

    /**
     * The grammar slot at which a getParserTree error is occurred.
     */
    private GrammarSlot errorSlot;

    /**
     * The last input index at which an error is occurred.
     */
    private int errorIndex;

    private String errorDescription;

    private final Deque<Descriptor<T>> descriptorPool;

    private final Deque<Descriptor<T>> descriptorsStack;

    private final IEvaluatorContext ctx;

    private final Configuration config;

    private final ParserLogger logger = ParserLogger.getInstance();

    private final ResultOps<T> resultOps;

    private boolean hasParseError;

    private Input input;

    public IguanaRuntime(Configuration config, ResultOps<T> resultOps) {
        this.config = config;
        this.resultOps = resultOps;
        this.descriptorsStack = new ArrayDeque<>(512);
        this.descriptorPool = new ArrayDeque<>(512);
        this.ctx = GLLEvaluator.getEvaluatorContext(config);
    }

    public Result run(Input input, Nonterminal start, GrammarGraph grammarGraph, Map<String, Object> map, boolean global) {
        this.input = input;

        IEvaluatorContext ctx = getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        int inputLength = input.length() - 1;

        Environment env = ctx.getEmptyEnvironment();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            env = env._declare(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Expression> entry : grammarGraph.getGlobals().entrySet()) {
            env = env._declare(entry.getKey(), entry.getValue().interpret(ctx, input));
        }

        StartGSSNode<T> startGSSNode;

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
                    throw new RuntimeException("No top level definition exists for " + start.getName() + " " + parameters);
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

        while (hasDescriptor()) {
            Descriptor<T> descriptor = nextDescriptor();
            logger.processDescriptor(descriptor);
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(), descriptor.getEnv(), this);
        }

        grammarGraph.clear();
        descriptorPool.clear();
        descriptorsStack.clear();

        T result = startGSSNode.getResult(inputLength);
        hasParseError = result == null || result.getRightExtent() != input.length() - 1;
        return result;
    }

    /**
     * Replaces the previously reported getParserTree error with the new one if the
     * inputIndex of the new getParserTree error is greater than the previous one. In
     * other words, we throw away an error if we find an error which happens at
     * the next position of input.
     *
     */
    public void recordParseError(int i, GrammarSlot slot, GSSNode<T> u, String description) {
        if (i >= this.errorIndex) {
            this.errorIndex = i;
            this.errorSlot = slot;
            this.errorDescription = description;
            logger.error(errorSlot, errorIndex, errorDescription);
        }
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
                return gssNode != null? new DummyGSSEdge<>(returnSlot, gssNode) : new CyclicDummyGSSEdges<>();
            } else {
                return gssNode != null? new DummyGSSEdgeWithEnv<>(returnSlot, gssNode, env) : new CyclicDummyGSSEdgesWithEnv<>(env);
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

    public ParseError getParseError() {
        if (!hasParseError) return null;
        return new ParseError(errorSlot, errorIndex, input.getLineNumber(errorIndex), input.getColumnNumber(errorIndex), errorDescription);
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
                System.out.printf("Popped Elements (min: %d, max: %d, mean: %.2f)%n", (int) poppedElementStats[0], (int) poppedElementStats[1], poppedElementStats[2]);

            if (gssEdgesStats == null)
                System.out.println("GSS Edges: empty");
            else
                System.out.printf("GSS Edges (min: %d, max: %d, mean: %.2f)%n", (int) gssEdgesStats[0], (int) gssEdgesStats[1], gssEdgesStats[2]);
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
            System.out.println(gssNode + ", edges: " + gssNode.countGSSEdges() + ", poppedElements: " + gssNode.countPoppedElements());
        }
    }

    private static double[] stats(Iterable<GSSNode> gssNodes, Function<GSSNode, Integer> f) {
        if (!gssNodes.iterator().hasNext()) return null;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int sum = 0;
        int count = 0;

        for (GSSNode gssNode : gssNodes) {
            min = Integer.min(min, f.apply(gssNode));
            max = Integer.max(max, f.apply(gssNode));
            sum += f.apply(gssNode);
            count++;
        }

        return new double[]{min, max, (double) sum / count};
    }
}
