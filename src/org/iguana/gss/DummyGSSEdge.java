package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

/**
 * GSSEdge that has the dummy node as its result.
 * The inputIndex of these GSS instances are the same as the input index of the destination GSS node because
 * the call is being made at the same input index.
 */
public class DummyGSSEdge<T extends Result> implements GSSEdge<T> {

    private final BodyGrammarSlot returnSlot;
    private final GSSNode<T> destination;

    public DummyGSSEdge(BodyGrammarSlot returnSlot, GSSNode<T> destination) {
        this.returnSlot = returnSlot;
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
        return destination.getInputIndex();
    }
}
