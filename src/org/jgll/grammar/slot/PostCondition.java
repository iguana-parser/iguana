package org.jgll.grammar.slot;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public interface PostCondition {

	public boolean execute(GLLParser parser, GLLLexer lexer);
	
	public void addPostCondition(SlotAction<Boolean> preCondition);
	
}
