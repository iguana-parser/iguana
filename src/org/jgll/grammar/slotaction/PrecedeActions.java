package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.RunnableAutomaton;

public class PrecedeActions {

	public static SlotAction<Boolean> fromRegularExpression(final RegularExpression regex, final Condition condition) {
		
		final RunnableAutomaton r = regex.toAutomaton().reverse().getRunnableAutomaton();

		return new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
				return r.matchBackwards(lexer.getInput(), inputIndex - 1) == -1;
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
