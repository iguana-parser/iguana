package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.PositionalCondition;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;

public class StartOfLineAction implements SlotAction<Boolean> {

	private PositionalCondition condition;

	public StartOfLineAction(PositionalCondition condition) {
		this.condition = condition;
	}
	
	@Override
	public Boolean execute(GLLParser parser, Lexer lexer, GSSNode gssNode, int inputIndex) {
		return !lexer.getInput().isStartOfLine(inputIndex);
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
		return "new StartOfLineAction(" + condition.getConstructorCode() + ");";
	}
	
}
