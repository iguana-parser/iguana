package org.jgll.regex;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;


public class RegexStar extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexStar(RegularExpression regexp) {
		this.regexp = regexp;
	}
	
	@Override
	public NFA toNFA() {
		return createNFA();
	}
	
	/**
	 * 
	 * @return
	 */
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		NFA nfa = regexp.toNFA();
		
		startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
		nfa.getEndState().setFinalState(false);
		
		nfa.getEndState().addTransition(Transition.emptyTransition(finalState));
		
		nfa.getEndState().addTransition(Transition.emptyTransition(nfa.getStartState()));
		
		startState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
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
		return regexp + "*";
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

	@Override
	public RegexStar copy() {
		return new RegexStar(regexp.copy());
	}
	
}
