package org.jgll.regex;

public class Transition {
	
	private int start;
	private int end;
	private State destination;
	
	private int id;
	
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public State getDestination() {
		return destination;
	}
	
	public boolean isEpsilonTransition() {
		return start == -1;
	}
	
	public boolean isLoop(State source) {
		return source == destination;
	}
	
	@Override
	public String toString() {
		
		if(isEpsilonTransition()) {
			return "-1";
		}
		
		if(end < Character.MAX_VALUE) {
			return (start == end) ? (char) start + "" : "[" + (char) start + "-" + (char) end + "]";
		}
		
		return (start == end) ? start + "" : "[" + start + "-" + end + "]";
	}
}
