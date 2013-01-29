package org.jgll.util;

import dk.brics.automaton.RunAutomaton;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class AutomatonUtil {
	
	/**
	 * Computes the shortest match from the provided offset for the string s using the RunAutomaton r.
	 * 
	 * @return  The length of the shortest match from the provided offset.
	 * 			-1 if no such shortest match exists.
	 * 
	 * @throws IllegalArgumentException if the offset is greater than or equals to the length of the string.
	 * 
	 */
	public static int getShortestMatch(RunAutomaton r, String s, int offset) {

		if(offset >= s.length()) {
			throw new IllegalArgumentException("The provided offset " + offset + " is greater than the length (" + s.length()  + ") of the String");
		}
		
		int state = r.getInitialState(); 
		int length = -1;
			
		int k = 1;
		for(int i = offset; i < s.length(); i++, k++) {
			state = r.step(state, s.charAt(i));
			
			if(state == -1) {
				break;
			}
			
			if(r.isAccept(state)) {
				length = k;
				break;
			}
		}
		
		return length;
	}
}
