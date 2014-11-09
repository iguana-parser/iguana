package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.automaton.RunnableAutomaton;

public class RegularExpressionNotPrecedeAction implements SlotAction<Boolean> {

	private final RunnableAutomaton r;
	private RegularExpressionCondition condition;
	
	public RegularExpressionNotPrecedeAction(RegularExpressionCondition condition) {
		this.condition = condition;
		this.r = condition.getRegularExpression().getAutomaton().reverse().getRunnableAutomaton();
	}

	@Override
	public Boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex) {
		if (inputIndex == 0) {
			return false;
		}
		return r.matchBackwards(lexer.getInput(), inputIndex - 1) >= 0;
	}

	@Override
	public Condition getCondition() {
		return condition;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		
		if(!(obj instanceof SlotAction)) return false;
		
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
		return "new RegularExpressionNotPrecedeAction(" + condition.getConstructorCode() + ")";
	}
	
}
