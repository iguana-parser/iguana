package org.iguana.parser;

import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;
import org.iguana.util.ParserLogger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ParserRuntimeImpl<T> implements ParserRuntime<T> {

    /**
     * The grammar slot at which a parse error is occurred.
     */
    private GrammarSlot<T> errorSlot;

    /**
     * The last input index at which an error is occurred.
     */
    private int errorIndex;

    private Input errorInput;

    /**
     * The current GSS node at which an error is occurred.
     */
    private GSSNode<T> errorGSSNode;

    private final Deque<Descriptor<T>> descriptorsStack;

    private final GrammarGraph<T> grammarGraph;

    private final IEvaluatorContext ctx;

    private final Configuration config;

    private final ParserLogger logger = ParserLogger.getInstance();

    public ParserRuntimeImpl(GrammarGraph<T> grammarGraph, Configuration config, IEvaluatorContext ctx) {
        this.grammarGraph = grammarGraph;
        this.descriptorsStack = new ArrayDeque<>();
        this.ctx = ctx;
        this.config = config;
    }

    /**
     * Replaces the previously reported parse error with the new one if the
     * inputIndex of the new parse error is greater than the previous one. In
     * other words, we throw away an error if we find an error which happens at
     * the next position of input.
     *
     */
    @Override
    public void recordParseError(Input input, int i, GrammarSlot<T> slot, GSSNode<T> u) {
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
    public Descriptor<T> nextDescriptor() {
        return descriptorsStack.pop();
    }

    @Override
    public final void scheduleDescriptor(Descriptor<T> descriptor) {
        descriptorsStack.push(descriptor);
        logger.descriptorAdded(descriptor);
    }

    @Override
    public Iterable<GSSNode<T>> getGSSNodes() {
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
    public GrammarGraph<T> getGrammarGraph() {
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
    public ParseError getParseError() {
        return new ParseError(errorSlot, errorInput == null ? Input.empty() : errorInput, errorIndex, errorGSSNode);
    }

    @Override
    public ParseStatistics getParseStatistics(Timer timer) {
        return ParseStatistics.builder()
                              .setNanoTime(timer.getNanoTime())
                              .setUserTime(timer.getUserTime())
                              .setSystemTime(timer.getSystemTime())
                              .setMemoryUsed(getMemoryUsed())
                              .setDescriptorsCount(logger.getDescriptorsCount())
                              .setGSSNodesCount(logger.getCountGSSNodes() + 1) // + start gss node
                              .setGSSEdgesCount(logger.getCountGSSEdges())
                              .setNonterminalNodesCount(logger.getCountNonterminalNodes())
                              .setTerminalNodesCount(logger.getCountTerminalNodes())
                              .setIntermediateNodesCount(logger.getCountIntermediateNodes())
                              .setPackedNodesCount(logger.getCountPackedNodes())
                              .setAmbiguousNodesCount(logger.getCountAmbiguousNodes())
                              .build();
    }

    private static int getMemoryUsed() {
        int mb = 1024 * 1024;
        Runtime runtime = Runtime.getRuntime();
        return (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

}
