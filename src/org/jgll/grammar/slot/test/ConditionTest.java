package org.jgll.grammar.slot.test;

import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public interface ConditionTest {
	
	public boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex);
	
	public Iterable<SlotAction<Boolean>> getConditions();
	
}
