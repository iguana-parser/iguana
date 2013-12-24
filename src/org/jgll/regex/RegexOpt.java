package org.jgll.regex;


public class RegexOpt implements RegularExpression {

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
		regexp.toNFA().getEndState().addTransition(Transition.emptyTransition(finalState));
		
		startState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
	}
	
}
