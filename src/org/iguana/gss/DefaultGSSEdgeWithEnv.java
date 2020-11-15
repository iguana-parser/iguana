package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public class DefaultGSSEdgeWithEnv<T extends Result> extends DefaultGSSEdge<T> {

    private final Environment env;

    public DefaultGSSEdgeWithEnv(BodyGrammarSlot returnSlot, T result, GSSNode<T> destination, Environment env) {
        super(returnSlot, result, destination);
        this.env = env;
    }

    @Override
    public Environment getEnv() {
        return env;
    }
}
