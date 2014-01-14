package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.recognizer.RecognizerFactory;
import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;

public class NotMatchActions {

	  public static void fromGrammarSlot(BodyGrammarSlot slot, final BodyGrammarSlot ifNot, final Condition condition) {

			slot.addPopAction(new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;
				private GLLRecognizer recognizer;

				@Override
				public Boolean execute(GLLParser parser, GLLLexer lexer) {
					
					if(recognizer == null) {
						recognizer = RecognizerFactory.contextFreeRecognizer(parser.getGrammar());						
					}
					
					return recognizer.recognize(lexer.getInput(), parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex(), ifNot);
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
	  
		public static void fromRegularExpression(BodyGrammarSlot slot, final RegularExpression regex, final Condition condition) {
			
			final Matcher matcher = regex.toAutomaton().getMatcher();
			
				slot.addPopAction(new SlotAction<Boolean>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean execute(GLLParser parser, GLLLexer lexer) {
						return matcher.match(lexer.getInput(), parser.getCurrentGSSNode().getInputIndex(), parser.getCurrentInputIndex());
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
