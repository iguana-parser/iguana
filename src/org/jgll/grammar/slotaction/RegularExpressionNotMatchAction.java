package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.automaton.RunnableAutomaton;

public class RegularExpressionNotMatchAction implements SlotAction<Boolean> {

	private final RegularExpressionCondition condition;
	private final RunnableAutomaton r;

	public RegularExpressionNotMatchAction(RegularExpressionCondition condition) {
		this.condition = condition;
		r = condition.getRegularExpression().getAutomaton().getRunnableAutomaton();
	}
	
	@Override
	public Boolean execute(GLLParser parser, Lexer lexer, GSSNode gssNode, int inputIndex) {
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

	@Override
	public String getConstructorCode() {
		return "new RegularExpressionNotMatchAction(" + condition.getConstructorCode() + ")";
	}
	
}
