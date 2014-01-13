package org.jgll.grammar.symbol;

import java.util.BitSet;

import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;

public class Epsilon extends AbstractSymbol implements Terminal {

	private static final String EPSILON = "epsilon";
	
	public static final int TOKEN_ID = 0;

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
	public Automaton toNFA() {
		return createNFA();
	}
	
	private Automaton createNFA() {
		State state = new State(true);
		return new Automaton(state);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public RegularExpression copy() {
		return this;
	}
}
