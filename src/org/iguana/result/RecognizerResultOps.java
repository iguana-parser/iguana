package org.iguana.result;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.ParserLogger;

public class RecognizerResultOps implements ResultOps<RecognizerResult> {

    private ParserLogger logger = ParserLogger.getInstance();

    private static final RecognizerResult dummy = RecognizerResult.of(-1);

    @Override
    public RecognizerResult dummy() {
        return dummy;
    }

    @Override
    public RecognizerResult base(TerminalGrammarSlot slot, int start, int end) {
        logger.terminalNodeAdded();
        return RecognizerResult.of(end);
    }

    @Override
    public RecognizerResult merge(RecognizerResult current, RecognizerResult result1, RecognizerResult result2, BodyGrammarSlot<RecognizerResult> slot) {
        logger.packedNodeAdded();
        if (current == null) logger.intermediateNodeAdded();
        return result2;
    }

    @Override
    public RecognizerResult convert(RecognizerResult current, RecognizerResult result, EndGrammarSlot slot, Object value) {
        logger.packedNodeAdded();
        if (current == null) {
            logger.nonterminalNodeAdded();
            if (value == null) {
                return result;
            }
            return RecognizerResult.of(result.getIndex(), value);
        }
        return result;
    }

    @Override
    public int getRightIndex(RecognizerResult result) {
        return result.getIndex();
    }

    @Override
    public int getRightIndex(RecognizerResult result, GSSNode<RecognizerResult> gssNode) {
        if (result == dummy) {
            return gssNode.getInputIndex();
        }

        return result.getIndex();
    }

    @Override
    public Object getValue(RecognizerResult result) {
        return result.getValue();
    }
}
