package org.jgll.grammar;


public class TerminalFactory {
	
	public  static Terminal from(int c) {
		return new Character(c);
	}
	
	public static Terminal get(int start, int end) {
		if(start == end) {
			return new Character(start);
		}
		return new Range(start, end);
	}
}
