package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.util.logging.LoggerWrapper;


public class PrecedeActions {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(PrecedeActions.class);
		
	public static void fromRegularExpression(BodyGrammarSlot slot, final RegularExpression regex, final Condition condition) {
		
		log.debug("Precede restriction added %s <<! %s", regex, slot);
		
		final Matcher matcher = regex.toAutomaton().reverse().getMatcher();

		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
				return matcher.matchBackwards(lexer.getInput(), inputIndex - 1) == -1;
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
