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

public interface ParserRuntime<T> {

    boolean hasDescriptor();

    Descriptor<T> nextDescriptor();

    void scheduleDescriptor(Descriptor<T> descriptor);

    void recordParseError(Input input, int i, GrammarSlot<T> slot, GSSNode<T> u);

    Iterable<GSSNode<T>> getGSSNodes();

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

    ParseError getParseError();

    ParseStatistics getParseStatistics(Timer timer);

    Configuration getConfiguration();

}
