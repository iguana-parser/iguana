package org.jgll.grammar.slotaction;

import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbols.Keyword;
import org.jgll.grammar.symbols.Terminal;
import org.jgll.parser.GLLParserInternals;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;


public class NotPrecedeActions {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(NotPrecedeActions.class);
	
	public static void fromTerminal(BodyGrammarSlot slot, final Terminal terminal, final Condition condition) {
		log.debug("Precede restriction added %s <<! %s", terminal, slot);
		
		BitSet testSet = new BitSet();
		testSet.or(terminal.asBitSet());
		
		final BitSet set = testSet;
		
		
		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return false;
				}
			
				return set.get(input.charAt(ci - 1));
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
	
	public static void fromKeywordList(BodyGrammarSlot slot, final List<Keyword> list, final Condition condition) {
		
		log.debug("Precede restriction added %s <<! %s", list, slot);
		
		slot.addPreCondition(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				int ci = parser.getCurrentInputIndex();
				if (ci == 0) {
					return false;
				}
				
				for(Keyword keyword : list) {
					if(input.matchBackward(ci, keyword.getChars())) {
						return true;
					}
				}
				
				return false;
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
