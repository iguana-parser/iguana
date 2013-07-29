package org.jgll.grammar;


public class TerminalFactory {
	
	public  static Terminal from(int c) {
		return new Character(c);
	}
	
	public static Terminal from(String s) {
		return new Keyword(s);
	}
	
	public static Terminal from(int[] chars) {
		return new Keyword(chars);
	}
	
	public static Terminal get(int start, int end) {
		if(start == end) {
			return new Character(start);
		}
		return new Range(start, end);
	}
}
