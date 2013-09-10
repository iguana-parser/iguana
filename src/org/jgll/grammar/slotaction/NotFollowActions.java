package org.jgll.grammar.slotaction;

import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.Keyword;
import org.jgll.grammar.Terminal;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.util.Input;


public class NotFollowActions {
	
	public static void fromGrammarSlot(BodyGrammarSlot slot, final BodyGrammarSlot firstSlot) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				GLLRecognizer recognizer = RecognizerFactory.prefixContextFreeRecognizer();
				return recognizer.recognize(input, parser.getCurrentInputIndex(), input.size(), firstSlot);
			}
		});
	 }
	
	
	public static void fromKeywordList(BodyGrammarSlot slot, final List<Keyword> list) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				for(Keyword s : list) {
					if(input.match(parser.getCurrentInputIndex(), s.getChars())) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	
	public static void fromTerminalList(BodyGrammarSlot slot, List<Terminal> list) {
		
		BitSet testSet = new BitSet();
		
		for(Terminal t : list) {
			testSet.or(t.asBitSet());
		}
		
		final BitSet set = testSet;
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, Input input) {
				return set.get(input.charAt(parser.getCurrentInputIndex()));
			}
		});
		
	}



}
