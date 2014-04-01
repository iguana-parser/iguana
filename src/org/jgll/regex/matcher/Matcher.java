package org.jgll.regex.matcher;

import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateAction;
import org.jgll.util.Input;

public interface Matcher {
	
	public static final int LONGEST_MATCH = 0;
	
	public static final int SHORTEST_MATCH = 1;
	
	/**
	 * Indicates whether the complete given input can be matched. 
	 */
	public boolean match(Input input);
	
	/**
	 * Returns the length of the longest match from the given input index.
	 * 
	 * @return -1 if no match is found
	 */
	public int match(Input input, int inputIndex);
		
	/**
	 * Returns true if can match the given input from the provided start position (inclusive)
	 * to the provided end position (exclusive).
	 *  
	 */
	public boolean match(Input input, int start, int end);
	
	public int matchBackwards(Input input, int inputIndex);
	
	public Matcher setMode(int mode);
	
	public void addStateAction(State state, StateAction action);
	
	public Matcher copy();
}
