package org.jgll.regex;

import java.util.List;

public class Sequence implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private List<RegularExpression> regularExpressions;
	
	private final NFA nfa;
	
	public Sequence() {
		this.nfa = createNFA();
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
		
		State currentState = startState;
		
		for(RegularExpression regexp : regularExpressions) {
			currentState.addTransition(Transition.emptyTransition(regexp.toNFA().getStartState()));
			currentState = regexp.toNFA().getEndState();
		}
		
		currentState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
	}

}
