package org.jgll.regex;

import java.io.Serializable;

import org.jgll.util.Input;


public class TrueMatcher implements Matcher, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;

	@Override
	public boolean match(Input input) {
		return true;
	}

	@Override
	public int match(Input input, int inputIndex) {
		return 0;
	}

	@Override
	public boolean match(Input input, int start, int end) {
		return true;
	}

	@Override
	public int matchBackwards(Input input, int inputIndex) {
		return 0;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Matcher setMode(int mode) {
		return this;
	}
	
	@Override
	public void addStateAction(State state, StateAction action) {
		// Do nothing
	}
	
	@Override
	public Matcher copy() {
		return this;
	}

}
