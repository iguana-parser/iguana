package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;


public class Epsilon extends AbstractRegularExpression {

	public static final int TOKEN_ID = 0;

	private static final long serialVersionUID = 1L;
	
	private static Epsilon instance;
	
	public static Epsilon getInstance() {
		if(instance == null) {
			instance = new Epsilon();
		}
		
		return instance;
	}
	
	private Epsilon() {
		super("epsilon");
	}

	@Override
	public Epsilon copy() {
		return this;
	}

	@Override
    protected Automaton createAutomaton() {
    	State state = new State(true).addRegularExpression(this);
        return new Automaton(state);
    }

	@Override
	public boolean isNullable() {
		return true;
	}
	
	@Override
	public Set<Range> getFirstSet() {
		HashSet<Range> firstSet = new HashSet<>();
		firstSet.add(new Range(-1, -1));
		return firstSet;
	}
	
}
