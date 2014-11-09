package org.jgll.grammar.slot.test;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.generator.ConstructorCode;

public interface ConditionTest extends ConstructorCode {
	
	public boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex);
	
}
