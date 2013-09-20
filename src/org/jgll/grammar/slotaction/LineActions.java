package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.util.Input;

public class LineActions {

	public static void addEndOfLine(BodyGrammarSlot slot, final Condition condition) {
		
		slot.addPopAction(new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return input.isEndOfLine(parser.getCurrentInputIndex());
			}

			@Override
			public Condition getCondition() {
				return condition;
			}
			
			@Override
			public boolean equals(Object obj) {
				if(this == obj) {
					return true;
				}
				
				if(!(obj instanceof SlotAction)) {
					return false;
				}
				
				@SuppressWarnings("unchecked")
				SlotAction<Boolean> other = (SlotAction<Boolean>) obj;
				return getCondition().equals(other.getCondition());
			}

		});
	}

	public static void addStartOfLine(BodyGrammarSlot slot, final Condition condition) {
		
		slot.addPopAction(new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return input.isStartOfLine(parser.getCurrentInputIndex());
			}

			@Override
			public Condition getCondition() {
				return condition;
			}
			
			@Override
			public boolean equals(Object obj) {
				if(this == obj) {
					return true;
				}
				
				if(!(obj instanceof SlotAction)) {
					return false;
				}
				
				@SuppressWarnings("unchecked")
				SlotAction<Boolean> other = (SlotAction<Boolean>) obj;
				return getCondition().equals(other.getCondition());
			}

		});
	}

}
