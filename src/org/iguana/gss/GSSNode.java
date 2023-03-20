package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;
import org.iguana.utils.input.Input;

public interface GSSNode<T extends Result> {

    NonterminalGrammarSlot getGrammarSlot();

    int getInputIndex();

    Object[] getData();

    void addGSSEdge(
        Input input,
        BodyGrammarSlot returnSlot,
        int i,
        GSSNode<T> destination,
        T w,
        Environment env,
        IguanaRuntime<T> runtime);

    boolean pop(Input input, EndGrammarSlot slot, T child, IguanaRuntime<T> runtime);

    boolean pop(Input input, EndGrammarSlot slot, T result, Object value, IguanaRuntime<T> runtime);

    int countGSSEdges();

    int countPoppedElements();

    Iterable<GSSEdge<T>> getGSSEdges();

    Iterable<T> getPoppedElements();
}
