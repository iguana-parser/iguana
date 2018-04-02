package org.iguana.parser;

import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.gss.GSSNode;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.ResultOps;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;

public interface ParserRuntime<T> {

    boolean hasDescriptor();

    Descriptor<T> nextDescriptor();

    void scheduleDescriptor(BodyGrammarSlot<T> grammarSlot, GSSNode<T> gssNode, T t, Environment env);

    void recordParseError(Input input, int i, GrammarSlot<T> slot, GSSNode<T> u);

    Object evaluate(Statement[] statements, Environment env, Input input);

    Object evaluate(DataDependentCondition condition, Environment env, Input input);

    Object evaluate(Expression expression, Environment env, Input input);

    Object[] evaluate(Expression[] arguments, Environment env, Input input);

    IEvaluatorContext getEvaluatorContext();

    Environment getEnvironment();

    void setEnvironment(Environment env);

    Environment getEmptyEnvironment();

    ParseError getParseError();

    ParseStatistics getParseStatistics(Timer timer);

    Configuration getConfiguration();

    int getDescriptorPoolSize();

    ResultOps<T> getResultOps();
}
