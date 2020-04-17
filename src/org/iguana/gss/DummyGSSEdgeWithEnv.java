package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public class DummyGSSEdgeWithEnv<T extends Result> extends DummyGSSEdge<T> {

    private final Environment env;

    public DummyGSSEdgeWithEnv(BodyGrammarSlot returnSlot, GSSNode<T> destination, Environment env) {
        super(returnSlot, destination);
        this.env = env;
    }

    @Override
    public Environment getEnv() {
        return env;
    }
}
