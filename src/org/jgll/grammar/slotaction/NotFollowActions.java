package org.jgll.grammar.slotaction;

import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegexAlt;

public class NotFollowActions {
	
	public static void fromGrammarSlot(BodyGrammarSlot slot, final BodyGrammarSlot firstSlot, final Condition condition) {
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			private GLLRecognizer recognizer;
			
			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer) {
				if(recognizer == null) {
					recognizer = RecognizerFactory.prefixContextFreeRecognizer(parser.getGrammar());
				}
				return recognizer.recognize(lexer.getInput(), parser.getCurrentInputIndex(), lexer.getInput().length(), firstSlot);
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
		
		RegexAlt<Keyword> alt = new RegexAlt<>(list);
		final Matcher matcher = alt.toNFA().getMatcher();
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer) {
				return matcher.match(lexer.getInput(), parser.getCurrentInputIndex()) >= 0;
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
			public Boolean execute(GLLParser parser, GLLLexer lexer) {
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
	
	public static void fromToken(BodyGrammarSlot slot, final int tokenID, final Condition condition) {
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Condition getCondition() {
				return condition;
			}
			
			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer) {
				return lexer.tokenLengthAt(parser.getCurrentInputIndex(), tokenID) > 0;
			}
		});
	}
	
	public static void fromTokenList(BodyGrammarSlot slot, final List<Integer> tokenIDs, final Condition condition) {
		
		final BitSet set = new BitSet();
		for(int i : tokenIDs) {
			set.set(i);
		}
		
		slot.addPopAction(new SlotAction<Boolean>() {
			
			private static final long serialVersionUID = 1L;
			
			@Override
			public Condition getCondition() {
				return condition;
			}
			
			@Override
			public Boolean execute(GLLParser parser, GLLLexer lexer) {
				return lexer.match(parser.getCurrentInputIndex(), set);
			}
		});
	}


}
