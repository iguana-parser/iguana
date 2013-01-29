package org.jgll.lexer;

public class LexicalMatch {
	
	private final String lexeme;
	
	private static LexicalMatch fail;
	
	public static LexicalMatch fail() {
		if(fail == null) {
			fail = new LexicalMatch(null);
		}
		return fail;
	}
	
	public static LexicalMatch success(String lexeme) {
		return new LexicalMatch(lexeme);
	}
	
	private LexicalMatch(String lexeme) {
		this.lexeme = lexeme;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public boolean isMatched() {
		return lexeme == null ? false : true;
	}
	
	@Override
	public String toString() {
		if(this == fail) {
			return "FAIL";
		}
		return lexeme;
	}
	
}
