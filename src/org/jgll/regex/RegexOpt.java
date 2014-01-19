package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;


public class RegexOpt extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	public RegexOpt(RegularExpression regexp) {
		super(regexp.getName() + "?");
		this.regexp = regexp;
	}
	
	@Override
	public Automaton toAutomaton() {
		return createNFA().addFinalStateActions(actions).addRegularExpression(this);
	}
	
	/**
	 * 
	 * @return
	 */
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true).addActions(actions);
		
		Automaton nfa = regexp.toAutomaton();
		startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
		
		Set<State> finalStates = nfa.getFinalStates();
		for(State s : finalStates) {
			s.setFinalState(false);
			s.addTransition(Transition.emptyTransition(finalState));			
		}
		
		startState.addTransition(Transition.emptyTransition(finalState));
		
		return new Automaton(startState);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public RegexOpt copy() {
		return new RegexOpt(regexp.copy());
	}
	
}
