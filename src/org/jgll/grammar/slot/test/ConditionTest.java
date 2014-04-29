package org.jgll.grammar.slot.test;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;

public interface ConditionTest {
	
	public boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex);
	
}
