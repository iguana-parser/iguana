package org.jgll.regex;

public class Transition {
	
	private int start;
	private int end;
	
	public Transition(int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public Transition(int c) {
		this.start = c;
		this.end = c;
	}
}
