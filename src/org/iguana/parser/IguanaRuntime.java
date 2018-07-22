package org.iguana.parser;

import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.EnvironmentPool;
import org.iguana.datadependent.env.GLLEvaluator;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.gss.GSSNode;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.Configuration;
import org.iguana.util.ParserLogger;
import org.iguana.util.RunningTime;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class IguanaRuntime<T extends Result> {

    /**
     * The grammar slot at which a parse error is occurred.
     */
    private GrammarSlot errorSlot;

    /**
     * The last input index at which an error is occurred.
     */
    private int errorIndex;

    /**
     * The current GSS node at which an error is occurred.
     */
    private GSSNode<T> errorGSSNode;

    private final Deque<Descriptor<T>> descriptorPool;

    private final Deque<Descriptor<T>> descriptorsStack;

    private final IEvaluatorContext ctx;

    private final Configuration config;

    private final ParserLogger logger = ParserLogger.getInstance();

    private final ResultOps<T> resultOps;

    private RunningTime runningTime;

    public IguanaRuntime(Configuration config, ResultOps<T> resultOps) {
        this.config = config;
        this.resultOps = resultOps;
        this.descriptorsStack = new ArrayDeque<>(512);
        this.descriptorPool = new ArrayDeque<>(512);
        this.ctx = GLLEvaluator.getEvaluatorContext(config);
    }

    public Result run(Input input, GrammarGraph grammarGraph, Nonterminal nonterminal, Map<String, Object> map, boolean global) {
        EnvironmentPool.clean();
        grammarGraph.reset(input);

        IEvaluatorContext ctx = getEvaluatorContext();

        if (global)
            map.forEach(ctx::declareGlobalVariable);

        NonterminalGrammarSlot startSymbol = grammarGraph.getHead(nonterminal);

        if (startSymbol == null) {
            throw new RuntimeException("No nonterminal named " + nonterminal + " found");
        }

        Environment env = ctx.getEmptyEnvironment();

        GSSNode<T> startGSSNode;

        if (!global && !map.isEmpty()) {
            Object[] arguments = new Object[map.size()];

            int i = 0;
            for (String parameter : nonterminal.getParameters())
                arguments[i++] = map.get(parameter);

            startGSSNode = startSymbol.getGSSNode(0, arguments);
            env = ctx.getEmptyEnvironment().declare(nonterminal.getParameters(), arguments);
        } else {
            startGSSNode = startSymbol.getGSSNode(0);
        }

        ParserLogger logger = ParserLogger.getInstance();
        logger.reset();

        Timer timer = new Timer();
        timer.start();

        for (BodyGrammarSlot slot : startSymbol.getFirstSlots()) {
            scheduleDescriptor(slot, startGSSNode, getResultOps().dummy(), env);
        }

        while (hasDescriptor()) {
            Descriptor<T> descriptor = nextDescriptor();
            logger.processDescriptor(descriptor);
            descriptor.getGrammarSlot().execute(input, descriptor.getGSSNode(), descriptor.getResult(), descriptor.getEnv(), this);
        }

        Result root = startGSSNode.getResult(input.length() - 1);

        timer.stop();
        runningTime = new RunningTime(timer.getNanoTime(), timer.getUserTime(), timer.getSystemTime());

        return root;
    }

    /**
     * Replaces the previously reported parse error with the new one if the
     * inputIndex of the new parse error is greater than the previous one. In
     * other words, we throw away an error if we find an error which happens at
     * the next position of input.
     *
     */
    public void recordParseError(int i, GrammarSlot slot, GSSNode<T> u) {
        if (i >= this.errorIndex) {
            logger.error(slot, i);
            this.errorIndex = i;
            this.errorSlot = slot;
            this.errorGSSNode = u;
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
        return new ParseError(errorSlot, errorIndex, errorGSSNode);
    }

    public ParseStatistics getParseStatistics() {
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
    }

    public int getMemoryUsed() {
        int mb = 1024 * 1024;
        java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
        return (int) ((runtime.totalMemory() - runtime.freeMemory()) / mb);
    }

    public RunningTime getRunningTime() {
        return runningTime;
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
}
