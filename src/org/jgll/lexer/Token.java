package org.jgll.lexer;


public class Token {
	
	private int tokenID;
	
	private int length;
	
	public Token(int tokenID, int length) {
		this.tokenID = tokenID;
		this.length = length;
	}
	
	public int getTokenID() {
		return tokenID;
	}
	
	public int getLength() {
		return length;
	}

}
