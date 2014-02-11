package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;
import org.jgll.regex.Transition;

public class EOF extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	public static final int TOKEN_ID = 1;

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
	
	@Override
	public Automaton toAutomaton() {
    	State startState = new State();
    	State endState = new State(true).addActions(actions).addRegularExpression(this);
    	startState.addTransition(new Transition(0, endState));
        return new Automaton(startState);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public RegularExpression copy() {
		return this;
	}
	
	@Override
	public Set<Integer> getFirstSet() {
		Set<Integer> firstSet = new HashSet<>();
		firstSet.add(0);
		return firstSet;
	}
}
