package org.jgll.regex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.CollectionsUtil;

public class Sequence extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;

	private final List<RegularExpression> regularExpressions;
	
	private final BitSet bitSet;
	
	public Sequence(List<RegularExpression> regularExpressions) {
		if(regularExpressions.size() == 0) {
			throw new IllegalArgumentException("The number of regular expressions in a sequence should be at least one.");
		}
		this.regularExpressions = regularExpressions;
		this.bitSet = calculateFirstChars();
	}
	
	public Sequence(RegularExpression...regularExpressions) {
		this(Arrays.asList(regularExpressions));
	}
	
	public List<RegularExpression> getRegularExpressions() {
		return regularExpressions;
	}

	@Override
	public NFA toNFA() {
		return createNFA();
	}
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);

		NFA[] nfas = new NFA[regularExpressions.size()];
		for(int i = 0; i < regularExpressions.size(); i++) {
			nfas[i] = regularExpressions.get(i).toNFA();
		}
		
		startState.addTransition(Transition.emptyTransition(nfas[0].getStartState()));
		
		// Middle regular expressions
		int i = 0;
		for(; i < regularExpressions.size() - 1; i++) {
			NFA nfa = nfas[i];
			NFA nextNFA = nfas[i+1];
			
			Set<State> finalStates = nfa.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.emptyTransition(nextNFA.getStartState()));
			}
		}
		
		for(State s : nfas[i].getFinalStates()) {
			s.setFinalState(false);
			s.addTransition(Transition.emptyTransition(finalState));
		}
				
		return new NFA(startState);
	}
	
	@Override
	public boolean isNullable() {
		for(RegularExpression regex : regularExpressions) {
			if(!regex.isNullable()) {
				return false;
			}
		}
		return true;
	}
	
	private BitSet calculateFirstChars() {
		BitSet set = new BitSet();
		for(RegularExpression s : regularExpressions) {
			set.or(s.asBitSet());
			
			if(!s.isNullable()) {
				break;
			}
		}
		return set;
	}

	@Override
	public BitSet asBitSet() {
		return bitSet;
	}

	@Override
	public String getName() {
		return CollectionsUtil.listToString(Arrays.asList(regularExpressions), " ");
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

	@Override
	public RegularExpression copy() {
		List<RegularExpression> copy = new ArrayList<>();
		for(RegularExpression regex : regularExpressions) {
			copy.add(regex.copy());
		}
		return new Sequence(copy);
	}
	
}
