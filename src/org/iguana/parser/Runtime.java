package org.iguana.parser;

import iguana.utils.benchmark.Timer;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.gss.GSSNode;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.result.Result;
import org.iguana.result.ResultOps;
import org.iguana.util.Configuration;
import org.iguana.util.ParseStatistics;

public interface Runtime<T extends Result> {

    boolean hasDescriptor();

    Descriptor<T> nextDescriptor();

    void scheduleDescriptor(BodyGrammarSlot grammarSlot, GSSNode<T> gssNode, T result, Environment env);

    void recordParseError(int i, GrammarSlot slot, GSSNode<T> u);

    void evaluate(Statement[] statements, Environment env, Input input);

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
