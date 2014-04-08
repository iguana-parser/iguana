package org.jgll.grammar.slot.test;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public interface ConditionTest {
	
	public boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex);
	
}
