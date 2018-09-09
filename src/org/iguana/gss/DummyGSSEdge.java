package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public class DummyGSSEdge<T extends Result> implements GSSEdge<T> {

    private final BodyGrammarSlot returnSlot;
    private final int inputIndex;
    private final GSSNode<T> destination;
    private final Environment env;

    public DummyGSSEdge(BodyGrammarSlot returnSlot, int inputIndex, GSSNode<T> destination, Environment env) {
        this.returnSlot = returnSlot;
        this.inputIndex = inputIndex;
        this.destination = destination;
        this.env = env;
    }

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public BodyGrammarSlot getReturnSlot() {
        return returnSlot;
    }

    @Override
    public GSSNode<T> getDestination() {
        return destination;
    }

    @Override
    public Environment getEnv() {
        return env;
    }

    @Override
    public int getInputIndex() {
        return inputIndex;
    }
}
