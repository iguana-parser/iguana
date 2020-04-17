package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * GSSEdge that has the dummy node as its result, but in contract to DummyGSSEdge, represents the special case of a
 * direct cycle due to left recursion.
 */
public class CyclicDummyGSSEdges<T extends Result> implements GSSEdge<T> {

    private final List<BodyGrammarSlot> returnSlots;

    public CyclicDummyGSSEdges() {
        returnSlots = new ArrayList<>(4);
    }

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public int getInputIndex() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BodyGrammarSlot getReturnSlot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public GSSNode<T> getDestination() {
        return null;
    }

    @Override
    public Environment getEnv() {
        return null;
    }

    List<BodyGrammarSlot> getReturnSlots() {
        return returnSlots;
    }

    void addReturnSlot(BodyGrammarSlot slot) {
        returnSlots.add(slot);
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s)", returnSlots, "$", "");
    }
}
