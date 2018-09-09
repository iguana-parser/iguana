package org.iguana.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.result.Result;

public interface GSSEdge<T extends Result> {

    T getResult();

    int getInputIndex();

    BodyGrammarSlot getReturnSlot();

    GSSNode<T> getDestination();

    Environment getEnv();

}
