package org.jgll.regex.matcher;

import java.io.Serializable;

import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateAction;
import org.jgll.util.Input;

public class CharacterMatcher implements Matcher, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final int c;
	
	public CharacterMatcher(int c) {
		this.c = c;
	}
	
	@Override
	public boolean match(Input input) {
		return input.charAt(0) == c;
	}

	@Override
	public int match(Input input, int inputIndex) {
		return input.charAt(inputIndex) == c ? 1 : -1;
	}

	@Override
	public boolean match(Input input, int start, int end) {
		return input.charAt(start) == c;
	}

	@Override
	public int matchBackwards(Input input, int inputIndex) {
		if(inputIndex > 0) {
			return input.charAt(inputIndex - 1) == c ? 1 : -1;
		}
		return -1;
	}

	@Override
	public Matcher setMode(int mode) {
		return null;
	}

	@Override
	public void addStateAction(State state, StateAction action) {
		
	}

	@Override
	public Matcher copy() {
		return new CharacterMatcher(c);
	}

}
