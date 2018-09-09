package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public class DummyGSSEdgeWithEnv<T extends Result> extends DummyGSSEdge<T> {

    private final Environment env;

    public DummyGSSEdgeWithEnv(BodyGrammarSlot returnSlot, int inputIndex, GSSNode<T> destination, Environment env) {
        super(returnSlot, inputIndex, destination);
        this.env = env;
    }

    @Override
    public Environment getEnv() {
        return env;
    }
}
