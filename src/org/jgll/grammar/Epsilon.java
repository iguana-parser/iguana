package org.jgll.grammar;

import java.util.BitSet;

public class Epsilon implements Terminal {

	private static final String EPSILON = "epsilon";

	private static final long serialVersionUID = 1L;
	
	private static Epsilon instance;
	
	public static Epsilon getInstance() {
		if(instance == null) {
			instance = new Epsilon();
		}
		
		return instance;
	}
	
	@Override
	public boolean match(int i) {
		return true;
	}

	@Override
	public String getMatchCode() {
		return "";
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public boolean isNonterminal() {
		return false;
	}

	@Override
	public String getName() {
		return EPSILON;
	}

	
	@Override
	public BitSet getTestSet() {
		throw new UnsupportedOperationException();
	}
	
}
