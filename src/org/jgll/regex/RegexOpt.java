package org.jgll.regex;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;


public class RegexOpt extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final RegularExpression regexp;
	
	private final NFA nfa;
	
	public RegexOpt(RegularExpression regexp) {
		this.regexp = regexp;
		this.nfa = createNFA();
	}
	
	@Override
	public NFA toNFA() {
		return nfa;
	}
	
	/**
	 * 
	 * @return
	 */
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		startState.addTransition(Transition.emptyTransition(regexp.toNFA().getStartState()));
		State e = regexp.toNFA().getEndState();
		e.setFinalState(false);
		e.addTransition(Transition.emptyTransition(finalState));
		
		startState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
	}

	@Override
	public boolean isNullable() {
		return true;
	}

	@Override
	public BitSet asBitSet() {
		return null;
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
