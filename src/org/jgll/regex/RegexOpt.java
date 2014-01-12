package org.jgll.regex;

import java.util.BitSet;
import java.util.Collection;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;


public class RegexOpt extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	public RegexOpt(RegularExpression regexp) {
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

	@Override
	public String getName() {
		return regexp.getName() + "?";
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

	@Override
	public RegexOpt copy() {
		return new RegexOpt(regexp.copy());
	}
	
}
