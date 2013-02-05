package org.jgll.grammar;


public class Character extends Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int c;

	public Character(int c) {
		this.c = c;
	}
	
	public int get() {
		return c;
	}
	
	@Override
	public boolean match(int i) {
		return c == i;
	}
	
	@Override
	public String toString() {
		return (char) c + "";
	}

	@Override
	public String getMatchCode() {
		return "I[ci] == " + c;
	}

}
