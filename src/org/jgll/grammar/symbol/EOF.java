package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;

public class EOF extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TOKEN_ID = 1;
	
	public static int VALUE = -1;
	
	private static EOF instance;
	
	public static EOF getInstance() {
		if(instance == null) {
			instance = new EOF();
		}
		return instance;
	}
	
	private EOF() {
		super("$");
	}
	
	protected Automaton createAutomaton() {
    	State startState = new State();
    	State endState = new State(true).addRegularExpression(this);
    	startState.addTransition(new Transition(VALUE, endState));
        return new Automaton(startState);		
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public EOF copy() {
		return this;
	}
	
	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		firstSet.add(new Range(0, 0));
		return firstSet;
	}
}
