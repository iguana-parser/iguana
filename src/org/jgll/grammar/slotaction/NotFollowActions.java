package org.jgll.grammar.slotaction;

import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;


public class NotFollowActions {
	
	public static void fromGrammarSlot(BodyGrammarSlot slot, final BodyGrammarSlot firstSlot, final Condition condition) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			private GLLRecognizer recognizer;
			
			@Override
			public Boolean execute(GLLParserInternals parser, GLLLexer lexer) {
				if(recognizer == null) {
					recognizer = RecognizerFactory.prefixContextFreeRecognizer(parser.getGrammar());
				}
				return recognizer.recognize(lexer.getInput(), parser.getCurrentInputIndex(), lexer.getInput().size(), firstSlot);
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
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, GLLLexer lexer) {
				for(Keyword s : list) {
					if(lexer.getInput().match(parser.getCurrentInputIndex(), s.getChars())) {
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
	
	public static void fromTerminal(BodyGrammarSlot slot, Terminal terminal, final Condition condition) {
		
		BitSet testSet = new BitSet();
		testSet.or(terminal.asBitSet());
		
		final BitSet set = testSet;
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParserInternals parser, GLLLexer lexer) {
				return set.get(lexer.getInput().charAt(parser.getCurrentInputIndex()));
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
	
	public static void fromToken(BodyGrammarSlot slot, int tokenID, final Condition condition) {
		slot.addPopAction(new SlotAction<Boolean>() {
			
			@Override
			public Condition getCondition() {
				return null;
			}
			
			@Override
			public Boolean execute(GLLParserInternals parser, GLLLexer lexer) {
				return null;
			}
		});
	}

}
