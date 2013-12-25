package org.jgll.regex;

public class Transition {
	
	private int start;
	private int end;
	private State destination;
	
	public Transition(int start, int end, State destination) {
		this.start = start;
		this.end = end;
		this.destination = destination;
	}
	
	public Transition(int c, State destination) {
		this(c, c, destination);
	}
	
	public static Transition emptyTransition(State destination) {
		return new Transition(-1, destination);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public State getDestination() {
		return destination;
	}
	
	public boolean isEpsilonTransition() {
		return start == -1;
	}
}
