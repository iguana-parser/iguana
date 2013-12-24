package org.jgll.regex;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;


public class RegexStar extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	private final NFA nfa;
	
	public RegexStar(RegularExpression regexp) {
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
		regexp.toNFA().getEndState().addTransition(Transition.emptyTransition(finalState));
		
		finalState.addTransition(Transition.emptyTransition(startState));
		
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
	
}
