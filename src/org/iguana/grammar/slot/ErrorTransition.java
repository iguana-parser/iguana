package org.iguana.grammar.slot;

import org.iguana.datadependent.env.Environment;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.utils.input.Input;

public class ErrorTransition extends AbstractTransition {

    public ErrorTransition(BodyGrammarSlot origin, BodyGrammarSlot dest) {
        super(origin, dest);
    }

    @Override
    public String getLabel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends Result> void execute(Input input, GSSNode<T> u, T result, Environment env, IguanaRuntime<T> runtime) {
        dest.execute(input, u, result, runtime.getEnvironment(), runtime);
    }
}
