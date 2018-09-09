package org.iguana.gss;

import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.util.ParserLogger;

import java.util.Collections;

public class StartGSSNode<T extends Result> implements GSSNode<T> {

    private final NonterminalGrammarSlot slot;
    private final int inputIndex;

    private final IntHashMap<T> poppedElements;

    public StartGSSNode(NonterminalGrammarSlot slot, int inputIndex) {
        this.slot = slot;
        this.inputIndex = inputIndex;
        poppedElements = new OpenAddressingIntHashMap<>();
    }

    @Override
    public NonterminalGrammarSlot getGrammarSlot() {
        return slot;
    }

    @Override
    public int getInputIndex() {
        return inputIndex;
    }

    @Override
    public Object[] getData() {
        return null;
    }

    @Override
    public void addGSSEdge(Input input, BodyGrammarSlot returnSlot, int i, GSSNode<T> destination, T w, Environment env, IguanaRuntime<T> runtime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean pop(Input input, EndGrammarSlot slot, T child, IguanaRuntime<T> runtime) {
        return false;
    }

    @Override
    public boolean pop(Input input, EndGrammarSlot slot, T result, Object value, IguanaRuntime<T> runtime) {
        ParserLogger.getInstance().pop(this, result.getLeftExtent(), result, value);

        int index = result.getIndex();
        T poppedElement = poppedElements.get(index);

        if (poppedElement == null) {
            poppedElement = runtime.getResultOps().convert(null, result, slot, value);
            poppedElements.put(index, poppedElement);
            return true;
        } else {
            runtime.getResultOps().convert(poppedElement, result, slot, value);
            return false;
        }
    }

    public T getResult(int i) {
        return poppedElements.get(i);
    }

    @Override
    public int countGSSEdges() {
        return 0;
    }

    @Override
    public int countPoppedElements() {
        return poppedElements.size();
    }

    @Override
    public Iterable<GSSEdge<T>> getGSSEdges() {
        return Collections.emptyList();
    }

    @Override
    public Iterable<T> getPoppedElements() {
        return poppedElements.values();
    }

    @Override
    public String toString() {
        return String.format("(%s, %d)", slot, inputIndex);
    }
}
