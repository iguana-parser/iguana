package org.jgll.grammar.slot.test;

import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.generator.ConstructorCode;

public interface ConditionTest extends ConstructorCode {
	
	public boolean execute(GLLParser parser, Lexer lexer, GSSNode gssNode, int inputIndex);
	
}
