package org.jgll.grammar.slot;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public interface PreCondition {

	public boolean execute(GLLParser parser, GLLLexer lexer);
	
	public void addPreCondition(SlotAction<Boolean> preCondition);
	
}
