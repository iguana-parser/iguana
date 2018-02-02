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
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.NonterminalOrIntermediateNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;

public interface ParserRuntime {

    boolean hasDescriptor();

    Descriptor nextDescriptor();

    void scheduleDescriptor(Descriptor descriptor);

    void recordParseError(Input input, int i, GrammarSlot slot, GSSNode u);

    Iterable<GSSNode> getGSSNodes();

    /*
     *
     * Data-dependent GLL parsing
     *
     */
    Object evaluate(Statement[] statements, Environment env);

    Object evaluate(DataDependentCondition condition, Environment env);

    Object evaluate(Expression expression, Environment env);

    Object[] evaluate(Expression[] arguments, Environment env);

    IEvaluatorContext getEvaluatorContext();

    Environment getEnvironment();

    void setEnvironment(Environment env);

    Environment getEmptyEnvironment();

    GrammarGraph getGrammarGraph();

    void terminalNodeAdded(TerminalNode node);

    void nonterminalNodeAdded(NonterminalNode node);

    void intermediateNodeAdded(IntermediateNode node);

    void packedNodeAdded(Object slot, int pivot);

    void ambiguousNodeAdded(NonterminalOrIntermediateNode<?> node);

    void gssNodeAdded(GSSNode node);

    void gssEdgeAdded(GSSEdge edge);

    ParseError getParseError();

    ParseStatistics getParseStatistics(Timer timer);

    Configuration getConfiguration();

    default void log(Object obj) {
        log(String.valueOf(obj));
    }

    void log(String s);

    void log(String s, Object arg);

    void log(String s, Object arg1, Object arg2);

    void log(String s, Object arg1, Object arg2, Object arg3);

    void log(String s, Object arg1, Object arg2, Object arg3, Object arg4);

    void log(String s, Object... args);

}
