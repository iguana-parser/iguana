package org.jgll.regex;

import java.util.Set;

import org.jgll.grammar.symbol.AbstractRegularExpression;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public class RegexStar extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp) {
		super(regexp + "*");
		this.regexp = regexp;
	}
	
	@Override
	public Automaton toAutomaton() {
		return createNFA().addFinalStateActions(actions).addRegularExpression(this);
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		Automaton nfa = regexp.toAutomaton();
		
		startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
		
		Set<State> finalStates = nfa.getFinalStates();
		for(State s : finalStates) {
			s.setFinalState(false);
			s.addTransition(Transition.emptyTransition(finalState));
			s.addTransition(Transition.emptyTransition(nfa.getStartState()));
		}
		
		startState.addTransition(Transition.emptyTransition(finalState));
		
		return new Automaton(startState);
	}
	
	@Override
	public boolean isNullable() {
		return true;
	}

	public RegexStar copy() {
		return new RegexStar(regexp.copy());
	}
	
	@Override
	public Set<Range> getFirstSet() {
		return regexp.getFirstSet();
	}
	
}
