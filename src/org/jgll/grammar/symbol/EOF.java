package org.jgll.grammar.symbol;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
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
    	State endState = new State(StateType.FINAL);
    	startState.addTransition(new Transition(VALUE, endState));
        return new Automaton(startState, name).setRegularExpression(this);		
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		firstSet.add(new Range(VALUE, VALUE));
		return firstSet;
	}

	@Override
	public EOF withConditions(Set<Condition> conditions) {
		return this;
	}

	@Override
	public EOF withoutConditions() {
		return this;
	}
	
}
