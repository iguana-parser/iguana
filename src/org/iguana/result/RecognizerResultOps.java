package org.iguana.result;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;

public class RecognizerResultOps implements ResultOps<RecognizerResult> {

    private static final RecognizerResult dummy = new RecognizerResult() {
        @Override
        public int getIndex() {
            return -1;
        }

        @Override
        public int getLeftExtent() {
            return -1;
        }

        @Override
        public boolean isDummy() {
            return true;
        }

        @Override
        public Object getValue() {
            return null;
        }
    };

    @Override
    public RecognizerResult dummy() {
        return dummy;
    }

    @Override
    public RecognizerResult base(TerminalGrammarSlot slot, int start, int end) {
        return RecognizerResult.of(end);
    }

    @Override
    public RecognizerResult merge(RecognizerResult current, RecognizerResult result1, RecognizerResult result2, BodyGrammarSlot slot) {
        return result2;
    }

    @Override
    public RecognizerResult convert(RecognizerResult current, RecognizerResult result, EndGrammarSlot slot, Object value) {
        if (current == null) {
            if (value == null) {
                return result;
            }
            return RecognizerResult.of(result.getIndex(), value);
        }
        return result;
    }
}

