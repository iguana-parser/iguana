package org.jgll.grammar.slot.test;

import java.io.Serializable;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;

public class FalseConditionTest implements ConditionTest, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(GLLParser parser, GLLLexer lexer, GSSNode gssNode, int inputIndex) {
		return false;
	}

	@Override
	public String getConstructorCode() {
		return "new FalseConditionTest()";
	}

}
