package org.jgll.regex.matcher;

import java.io.Serializable;

import org.jgll.util.Input;


public class TrueMatcher implements Matcher, Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
	public Matcher setMode(int mode) {
		return this;
	}
	
	@Override
	public Matcher copy() {
		return this;
	}

}
