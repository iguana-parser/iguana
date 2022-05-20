package org.iguana.sppf;

import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.slot.TerminalGrammarSlot;

public class EpsilonTerminalNode extends TerminalNode {

    public EpsilonTerminalNode(int leftExtent) {
        super(leftExtent);
    }

    @Override
    public TerminalGrammarSlot getGrammarSlot() {
        return GrammarGraph.epsilonSlot;
    }

    @Override
    public int getRightExtent() {
        return getLeftExtent();
    }
}
