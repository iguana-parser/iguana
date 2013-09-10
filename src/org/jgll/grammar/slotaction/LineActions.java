package org.jgll.grammar.slotaction;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.util.Input;

public class LineActions {

	public static void addEndOfLine(BodyGrammarSlot slot) {
		
		slot.addPopAction(new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return input.isEndOfLine(parser.getCurrentInputIndex());
			}
		});
	}

	public static void addStartOfLine(BodyGrammarSlot slot) {
		
		slot.addPopAction(new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return input.isStartOfLine(parser.getCurrentInputIndex());
			}
		});
	}

}
