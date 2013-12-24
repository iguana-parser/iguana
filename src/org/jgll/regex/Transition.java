package org.jgll.regex;

public class Transition {
	
	public static final Transition EMPTY = new Transition(-1);
	
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
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
}
