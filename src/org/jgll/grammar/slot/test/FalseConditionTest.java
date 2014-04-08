package org.jgll.grammar.slot.test;

import java.io.Serializable;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class FalseConditionTest implements ConditionTest, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(GLLParser parser, GLLLexer lexer, int inputIndex) {
		return false;
	}

}
