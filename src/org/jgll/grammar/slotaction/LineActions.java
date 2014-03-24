package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class LineActions {

	public static SlotAction<Boolean> addEndOfLine(final Condition condition) {
		
		return new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
				return !lexer.getInput().isEndOfLine(inputIndex);
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
			
			@Override
			public String toString() {
				return condition.toString();
			}

		};
	}

	public static SlotAction<Boolean> addStartOfLine(final Condition condition) {
		
		return new SlotAction<Boolean>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
				return !lexer.getInput().isStartOfLine(inputIndex);
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
			
			@Override
			public String toString() {
				return condition.toString();
			}

		};
	}

}
