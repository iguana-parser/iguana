package org.jgll.regex;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class RegexAlt implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final List<RegularExpression> regularExpressions;
	
	private final NFA nfa;
	
	private final BitSet bitSet;
	
	public RegexAlt(List<RegularExpression> regularExpressions) {
		this.nfa = createNFA();
		this.regularExpressions = regularExpressions;
		this.bitSet = calculateBitSet();
	}
	
	public RegexAlt(RegularExpression...regularExpressions) {
		this(Arrays.asList(regularExpressions));
	}
	
	public List<RegularExpression> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	public NFA toNFA() {
		return nfa;
	}
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		for(RegularExpression regexp : regularExpressions) {
			startState.addTransition(Transition.emptyTransition(regexp.toNFA().getStartState()));
			regexp.toNFA().getEndState().addTransition(Transition.emptyTransition(finalState));
		}
		
		return new NFA(startState, finalState);
	}

	@Override
	public boolean isNullable() {
		for(RegularExpression regex : regularExpressions) {
			if(regex.isNullable()) {
				return true;
			}
		}
		return false;
	}
	
	private BitSet calculateBitSet() {
		BitSet set = new BitSet();
		for(RegularExpression s : regularExpressions) {
			set.or(s.asBitSet());
		}
		return set;
	}

	@Override
	public BitSet asBitSet() {
		return bitSet;
	}

}
