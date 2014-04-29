package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.RunnableAutomaton;

public class NotMatchActions {

	  public static SlotAction<Boolean> fromGrammarSlot(final Condition condition) {

			return new SlotAction<Boolean>() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public Boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex) {
					throw new UnsupportedOperationException();
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
	  
		public static SlotAction<Boolean> fromRegularExpression(final RegularExpression regex, final Condition condition) {
			
			final RunnableAutomaton r = regex.getAutomaton().getRunnableAutomaton();
			
			return	new SlotAction<Boolean>() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex) {
						int start = gssNode.getInputIndex();
						int end = inputIndex;
						return r.match(lexer.getInput(), start, end);
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
