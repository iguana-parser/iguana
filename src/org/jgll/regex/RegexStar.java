package org.jgll.regex;

import java.util.BitSet;
import java.util.Set;

import org.jgll.grammar.symbol.AbstractSymbol;


public class RegexStar extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp) {
		super(regexp + "*");
		this.regexp = regexp;
	}
	
	@Override
	public Automaton toNFA() {
		return createNFA();
	}
	
	/**
	 * 
	 * @return
	 */
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		Automaton nfa = regexp.toNFA();
		
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

	@Override
	public BitSet asBitSet() {
		return regexp.asBitSet();
	}

	public RegexStar copy() {
		return new RegexStar(regexp.copy());
	}
	
}
