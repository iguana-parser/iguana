package org.jgll.grammar;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;

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
	public String getName() {
		return EPSILON;
	}

	
	@Override
	public BitSet asBitSet() {
		return new BitSet();
	}

	@Override
	public Symbol addConditions(Collection<Condition> condition) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Condition> getConditions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Symbol addCondition(Condition condition) {
		throw new UnsupportedOperationException();
	}

}
