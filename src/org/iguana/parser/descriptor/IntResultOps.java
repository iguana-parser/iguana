package org.iguana.parser.descriptor;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.ParserLogger;

public class IntResultOps implements ResultOps<Integer> {

    private ParserLogger logger = ParserLogger.getInstance();

    @Override
    public Integer dummy(int i) {
        return i;
    }

    @Override
    public Integer base(TerminalGrammarSlot slot, int start, int end) {
        logger.terminalNodeAdded();
        return end;
    }

    @Override
    public Integer merge(Integer current, Integer result1, Integer result2, BodyGrammarSlot<Integer> slot) {
        logger.packedNodeAdded();
        if (current == null) logger.intermediateNodeAdded();
        return result2;
    }

    @Override
    public Integer convert(Integer current, Integer result, EndGrammarSlot slot, Object value) {
        logger.packedNodeAdded();
        if (current == null) logger.nonterminalNodeAdded();
        return result;
    }

    @Override
    public int getRightIndex(Integer result) {
        return result;
    }

    @Override
    public int getRightIndex(Integer result, GSSNode<Integer> gssNode) {
        return result;
    }

    @Override
    public Object getValue(Integer result) {
        return null;
    }
}
