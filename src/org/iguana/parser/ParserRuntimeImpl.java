package org.iguana.parser;

import iguana.parsetrees.sppf.IntermediateNode;
import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.NonterminalOrIntermediateNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import iguana.utils.logging.IguanaLogger;
import iguana.utils.logging.JavaUtilIguanaLogger;
import iguana.utils.logging.LogLevel;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.BenchmarkUtil;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParserRuntimeImpl implements ParserRuntime {

    /**
     * The grammar slot at which a parse error is occurred.
     */
    private GrammarSlot errorSlot;

    /**
     * The last input index at which an error is occurred.
     */
    private int errorIndex;

    private Input errorInput;

    /**
     * The current GSS node at which an error is occurred.
     */
    private GSSNode errorGSSNode;

    private final Deque<Descriptor> descriptorsStack;

    private final IguanaLogger logger;

    private final GrammarGraph grammarGraph;

    private final IEvaluatorContext ctx;

    private final Configuration config;

    public ParserRuntimeImpl(GrammarGraph grammarGraph, Configuration config, IEvaluatorContext ctx) {
        this.grammarGraph = grammarGraph;
        this.descriptorsStack = new ArrayDeque<>();
        this.ctx = ctx;
        this.config = config;
        if (config.getLogLevel() == LogLevel.None)
            logger = IguanaLogger.DEFAULT;
        else
            logger = new JavaUtilIguanaLogger("Iguana Logger", config.getLogLevel());
    }

    /**
     * Replaces the previously reported parse error with the new one if the
     * inputIndex of the new parse error is greater than the previous one. In
     * other words, we throw away an error if we find an error which happens at
     * the next position of input.
     *
     */
    @Override
    public void recordParseError(Input input, int i, GrammarSlot slot, GSSNode u) {
        if (i >= this.errorIndex) {
            logger.log("Error recorded at %s %d", slot, i);
            this.errorInput = input;
            this.errorIndex = i;
            this.errorSlot = slot;
            this.errorGSSNode = u;
        }
    }

    @Override
    public boolean hasDescriptor() {
        return !descriptorsStack.isEmpty();
    }

    @Override
    public Descriptor nextDescriptor() {
        return descriptorsStack.pop();
    }

    @Override
    public final void scheduleDescriptor(Descriptor descriptor) {
        descriptorsStack.push(descriptor);
        logger.log("Descriptor created: %s", descriptor);
        descriptorsCount++;
    }

    @Override
    public Iterable<GSSNode> getGSSNodes() {
        return grammarGraph.getNonterminals().stream().flatMap(s -> StreamSupport.stream(s.getGSSNodes().spliterator(), false)).collect(Collectors.toList());
    }

    @Override
    public IEvaluatorContext getEvaluatorContext() {
        return ctx;
    }

    @Override
    public Environment getEnvironment() {
        return ctx.getEnvironment();
    }

    @Override
    public void setEnvironment(Environment env) {
        ctx.setEnvironment(env);
    }

    @Override
    public Environment getEmptyEnvironment() {
        return ctx.getEmptyEnvironment();
    }

    @Override
    public GrammarGraph getGrammarGraph() {
        return grammarGraph;
    }

    @Override
    public Object evaluate(Statement[] statements, Environment env) {
        assert statements.length > 1;

        ctx.setEnvironment(env);

        int i = 0;
        while (i < statements.length) {
            statements[i].interpret(ctx);
            i++;
        }

        return null;
    }

    @Override
    public Object evaluate(DataDependentCondition condition, Environment env) {
        ctx.setEnvironment(env);
        return condition.getExpression().interpret(ctx);
    }

    @Override
    public Object evaluate(Expression expression, Environment env) {
        ctx.setEnvironment(env);
        return expression.interpret(ctx);
    }

    @Override
    public Object[] evaluate(Expression[] arguments, Environment env) {
        if (arguments == null) return null;

        ctx.setEnvironment(env);

        Object[] values = new Object[arguments.length];

        int i = 0;
        while (i < arguments.length) {
            values[i] = arguments[i].interpret(ctx);
            i++;
        }

        return values;
    }

    @Override
    public void terminalNodeAdded(TerminalNode node) {
        countTerminalNodes++;
        logger.log("Terminal node added %s", node);
    }

    @Override
    public void nonterminalNodeAdded(NonterminalNode node) {
        countNonterminalNodes++;
        logger.log("Nonterminal node added %s", node);
    }

    @Override
    public void intermediateNodeAdded(IntermediateNode node) {
        countIntermediateNodes++;
        logger.log("Intermediate node added %s", node);
    }

    @Override
    public void packedNodeAdded(Object slot, int pivot) {
        countPackedNodes++;
        logger.log("Packed node added (%s, %d)", slot, pivot);
    }

    @Override
    public void ambiguousNodeAdded(NonterminalOrIntermediateNode node) {
        countAmbiguousNodes++;
        logger.log("Ambiguous node added: %s", node);
//		System.out.println(String.format("Ambiguous node added: %s %s", node, input.getNodeInfo(node)));
//		org.iguana.util.Visualization.generateSPPFGraph("/Users/afroozeh/output", node, input);
//		for (PackedNode packedNode : node.getChildren()) {
//			System.out.println("   Packed node: " + packedNode.toString());
//			for (org.iguana.sppf.NonPackedNode child : packedNode.getChildren()) {
//				System.out.println(String.format("       %s %s", child, input.getNodeInfo(child)));
//			}
//		}
//		System.exit(0);
    }

    @Override
    public void gssNodeAdded(GSSNode node) {
        countGSSNodes++;
        logger.log("GSS node added %s", node);
    }

    @Override
    public void gssEdgeAdded(GSSEdge edge) {
        countGSSEdges++;
        logger.log("GSS Edge added %s", edge);
    }

    @Override
    public void log(String s) {
        logger.log(s);
    }

    @Override
    public void log(String s, Object arg) {
        logger.log(s, arg);
    }

    @Override
    public void log(String s, Object arg1, Object arg2) {
        logger.log(s, arg1, arg2);
    }

    @Override
    public void log(String s, Object arg1, Object arg2, Object arg3) {
        logger.log(s, arg1, arg2, arg3);
    }

    @Override
    public void log(String s, Object arg1, Object arg2, Object arg3, Object arg4) {
        logger.log(s, arg1, arg2, arg3, arg4);
    }

    @Override
    public void log(String s, Object...args) {
        logger.log(s, args);
    }

    @Override
    public ParseError getParseError() {
        return new ParseError(errorSlot, errorInput == null ? Input.empty() : errorInput, errorIndex, errorGSSNode);
    }

    @Override
    public ParseStatistics getParseStatistics(Timer timer) {
        return ParseStatistics.builder()
                              .setNanoTime(timer.getNanoTime())
                              .setUserTime(timer.getUserTime())
                              .setSystemTime(timer.getSystemTime())
                              .setMemoryUsed(BenchmarkUtil.getMemoryUsed())
                              .setDescriptorsCount(descriptorsCount)
                              .setGSSNodesCount(countGSSNodes + 1) // + start gss node
                              .setGSSEdgesCount(countGSSEdges)
                              .setNonterminalNodesCount(countNonterminalNodes)
                              .setTerminalNodesCount(countTerminalNodes)
                              .setIntermediateNodesCount(countIntermediateNodes)
                              .setPackedNodesCount(countPackedNodes)
                              .setAmbiguousNodesCount(countAmbiguousNodes)
                              .build();
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    private int descriptorsCount;

    private int countNonterminalNodes;

    private int countIntermediateNodes;

    private int countTerminalNodes;

    private int countPackedNodes;

    private int countAmbiguousNodes;

    private int countGSSNodes;

    private int countGSSEdges;

}
