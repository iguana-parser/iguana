package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public class DummyGSSEdge<T extends Result> implements GSSEdge<T> {

    private final BodyGrammarSlot returnSlot;
    private final int inputIndex;
    private final GSSNode<T> destination;

    public DummyGSSEdge(BodyGrammarSlot returnSlot, int inputIndex, GSSNode<T> destination) {
        this.returnSlot = returnSlot;
        this.inputIndex = inputIndex;
        this.destination = destination;
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
        return null;
    }

    @Override
    public int getInputIndex() {
        return inputIndex;
    }
}
