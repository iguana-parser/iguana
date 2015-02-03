package org.jgll.regex.automaton;

import java.io.Serializable;

import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.EOF;
import org.jgll.parser.HashFunctions;

public class Transition implements Comparable<Transition>, Serializable {
	
	private static final long serialVersionUID = 1L;

	private CharacterRange range;
	
	private State destination;
	
	private int id;
	
	public Transition(int start, int end, State destination) {
		this(CharacterRange.in(start, end), destination);
	}
	
	public Transition(int c, State destination) {
		this(c, c, destination);
	}
	
	public Transition(CharacterRange range, State destination) {
		if (range.getEnd() < range.getStart())
			throw new IllegalArgumentException("start cannot be less than end.");
		
		if (destination == null) 
			throw new IllegalArgumentException("Destination cannot be null.");

		this.range = range;
		this.destination = destination;
	}
	
	public static Transition epsilonTransition(State destination) {
		return new Transition(-1, destination);
	}
	
	public static Transition EOFTransition(State destination) {
		return new Transition(EOF.VALUE, destination);
	}
	
	public int getStart() {
		return range.getStart();
	}
	
	public int getEnd() {
		return range.getEnd();
	}
	
	public CharacterRange getRange() {
		return range;
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
		return range.getStart() == -1;
	}
	
	public boolean isLoop(State source) {
		return source == destination;
	}
	
	public boolean canMove(int c) {
		return range.contains(c);
	}
	
	public boolean overlaps(Transition t) {
		return range.overlaps(t.range);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;
		
		if(!(obj instanceof Transition))
			return false;
		
		Transition other = (Transition) obj;
		
		return range.equals(other.range);
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(range.getStart(), range.getEnd());
	}
	
	@Override
	public String toString() {
		
		if(isEpsilonTransition()) {
			return "-1";
		}
		
		return range.toString();
	}

	@Override
	public int compareTo(Transition t) {
		return range.compareTo(t.range);
	}
		
}
