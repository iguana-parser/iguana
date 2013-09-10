package org.jgll.grammar.slotaction;

import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.Keyword;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.util.Input;
import org.jgll.util.logging.LoggerWrapper;


public class NotPrecedeActions {

	private static final LoggerWrapper log = LoggerWrapper.getLogger(NotPrecedeActions.class);
	
	public static void fromTerminalList(BodyGrammarSlot slot, final List<Terminal> terminals) {
		log.debug("Precede restriction added %s <<! %s", terminals, slot);
		
		BitSet testSet = new BitSet();
		
		for(Terminal t : terminals) {
			testSet.or(t.asBitSet());
		}
		
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
		});
	}
	
	public static void fromKeywordList(BodyGrammarSlot slot, final List<Keyword> list) {
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
		});
	}
	
	
}
