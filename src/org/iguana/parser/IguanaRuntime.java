package org.iguana.parser;

import iguana.utils.input.Input;
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
import org.iguana.util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IguanaRuntime<T extends Result> {

    /**
     * The grammar slot at which a getParserTree error is occurred.
     */
    private GrammarSlot errorSlot;

    /**
     * The last input index at which an error is occurred.
     */
    private int errorIndex;

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

    // SPPF found in `T result = startGSSNode.getResult(v);`
    public Stream<Pair> no_sppf_run(Input input, GrammarGraph grammarGraph, Map<String, Object> map, boolean global) {
        this.input = input;

        IEvaluatorContext ctx = getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        NonterminalGrammarSlot startSymbol = grammarGraph.getStartSlot();

        Environment env = ctx.getEmptyEnvironment();

        List<DefaultGSSNode<T>> startGSSNodes = new ArrayList<>();
        for (Integer node : input.getStartVertices().collect(Collectors.toList())) {
            startGSSNodes.add(new DefaultGSSNode<T>(startSymbol, node));
        }

        startGSSNodes.forEach(node -> startSymbol.addStartGSSNode(node, node.getInputIndex()));

        List<BodyGrammarSlot> t = startSymbol.getFirstSlots();
        for (BodyGrammarSlot slot : t) {
            for (DefaultGSSNode<T> startGSSNode: startGSSNodes) {
                scheduleDescriptor(slot, startGSSNode, getResultOps().dummy(), env);
            }
        }

        while (hasDescriptor()) {
            Descriptor<T> descriptor = nextDescriptor();
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(), descriptor.getEnv(), this);
        }

        grammarGraph.clear();
        descriptorPool.clear();
        descriptorsStack.clear();

        final boolean[] empty = {true};
        Stream.Builder<Pair> results = Stream.builder();
        startGSSNodes.forEach(startGSSNode -> {
            startGSSNode.getPoppedElements().forEach(v -> {
                results.add(new Pair(startGSSNode.getInputIndex(), v.getIndex()));
                empty[0] = false;
            });
//        startGSSNodes.forEach(startGSSNode -> {
//            input.getFinalVertices().forEach(v -> {
//                if (startGSSNode.hasResult(v)) {
//                    results.add(new Pair(startGSSNode.getInputIndex(), v));
//                    empty[0] = false;
//                }
//            });
//                    for (Integer v: input.getFinalVertices()) {
//                T result = startGSSNode.getResult(v);
//                if (result != null) {
//                    results.put(new Pair(startGSSNode.getInputIndex(), v), result);
//                }
//            }
        });
//        hasParseError = results.build().;
        if (empty[0]) {
            return null;
        }
        //List<Pair> res = results.build().distinct().collect(Collectors.toList());
        return results.build();
    }

    public Map<Pair, Result> run(Input input, GrammarGraph grammarGraph, Map<String, Object> map, boolean global) {
        this.input = input;

        IEvaluatorContext ctx = getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        NonterminalGrammarSlot startSymbol = grammarGraph.getStartSlot();

        Environment env = ctx.getEmptyEnvironment();

        List<DefaultGSSNode<T>> startGSSNodes = new ArrayList<>();
        for (Integer node : input.getStartVertices().collect(Collectors.toList())) {
            startGSSNodes.add(new DefaultGSSNode<T>(startSymbol, node));
        }

        startGSSNodes.forEach(node -> startSymbol.addStartGSSNode(node, node.getInputIndex()));

//        ParserLogger logger = ParserLogger.getInstance();
//        logger.reset();

        List<BodyGrammarSlot> t = startSymbol.getFirstSlots();
        for (BodyGrammarSlot slot : t) {
            for (DefaultGSSNode<T> startGSSNode: startGSSNodes) {
                scheduleDescriptor(slot, startGSSNode, getResultOps().dummy(), env);
            }
        }

        while (hasDescriptor()) {
            Descriptor<T> descriptor = nextDescriptor();
            // logger.processDescriptor(descriptor);
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(), descriptor.getEnv(), this);
        }

        grammarGraph.clear();
        descriptorPool.clear();
        descriptorsStack.clear();

        Map<Pair, Result> results = new HashMap<>();

//        for (DefaultGSSNode<T> startGSSNode: startGSSNodes) {
//            for (Integer v: input.getFinalVertices()) {
//                T result = startGSSNode.getResult(v);
//                if (result != null) {
//                    results.put(new Pair(startGSSNode.getInputIndex(), v), result);
//                }
//            }
//        }
        startGSSNodes.forEach(startGSSNode -> {
            input.getFinalVertices().forEach(v -> {
                T result = startGSSNode.getResult(v);
                if (result != null) {
                    results.put(new Pair(startGSSNode.getInputIndex(), v), result);
                }
            });
//                    for (Integer v: input.getFinalVertices()) {
//                T result = startGSSNode.getResult(v);
//                if (result != null) {
//                    results.put(new Pair(startGSSNode.getInputIndex(), v), result);
//                }
//            }
        });
        hasParseError = results.isEmpty();
        if (hasParseError) {
            return null;
        }

        return results;
    }

    /**
     * Replaces the previously reported getParserTree error with the new one if the
     * inputIndex of the new getParserTree error is greater than the previous one. In
     * other words, we throw away an error if we find an error which happens at
     * the next position of input.
     */
    public void recordParseError(int i, GrammarSlot slot, GSSNode<T> u) {
        if (i >= this.errorIndex) {
            logger.error(slot, i);
            this.errorIndex = i;
            this.errorSlot = slot;
          //  logger.error(errorSlot, errorIndex);
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
       // logger.descriptorAdded(descriptor);
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
                return gssNode != null ? new DummyGSSEdgeWithEnv<>(returnSlot, gssNode, env) : new CyclicDummyGSSEdgesWithEnv<>(env);
            }
        }

        if (env.isEmpty()) {
            return new DefaultGSSEdge<>(returnSlot, result, gssNode);
        } else {
            return new DefaultGSSEdgeWithEnv<>(returnSlot, result, gssNode, env);
        }
    }

    public void evaluate(Statement[] statements, Environment env, Input input) {
        assert statements.length > 1;

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
        return new ParseError(errorSlot, input, errorIndex);
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


