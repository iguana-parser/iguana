package org.jgll.lexer;

public interface LexicalPattern {

	public String match(String input, int inputIndex);
	
}
