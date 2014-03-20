package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;


public class NotPrecedeActions {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(NotPrecedeActions.class);
		
	public static SlotAction<Boolean> fromRegularExpression(final RegularExpression regex, final Condition condition) {
		
		log.debug("Precede restriction added %s <<! %s", regex);
		
		final Matcher matcher = regex.toAutomaton().reverse().getMatcher();

		return new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
				return matcher.matchBackwards(lexer.getInput(), inputIndex - 1) >= 0;
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
		};
	}
}
