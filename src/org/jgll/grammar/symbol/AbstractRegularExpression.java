package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected Automaton automaton;

	public AbstractRegularExpression(String name) {
		this(name, Collections.<Condition>emptySet());
	}
	
	public AbstractRegularExpression(String name, Set<Condition> conditions) {
		super(name, conditions);
	}
	
	@Override
	public Automaton getAutomaton() {
		if (automaton == null) {
			automaton = combineConditions(createAutomaton());
		}
		return automaton;
	}
	
	protected abstract Automaton createAutomaton();

	protected Automaton combineConditions(Automaton a) {
		
		Automaton result = a;
		
		for(Condition condition : conditions) {
			if(condition.getType() == ConditionType.NOT_MATCH && condition instanceof RegularExpressionCondition) {
				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
				RegularExpression regex = regexCondition.getRegularExpression();
				result = AutomatonOperations.difference(result, regex.getAutomaton());
			} 
			else if (condition.getType() == ConditionType.NOT_FOLLOW && condition instanceof RegularExpressionCondition) {
				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
				RegularExpression regex = regexCondition.getRegularExpression();
				
				Automaton automaton = regex.getAutomaton().copy();

				for (State state : result.getFinalStates()) {
					automaton.getStartState().setAntiAcceptState(true);
					state.addTransition(Transition.epsilonTransition(automaton.getStartState()));
				}
				
				for (State state : automaton.getFinalStates()) {
					state.setAntiAcceptState(true);
					state.setFinalState(false);
				}
				
				result = new Automaton(result.getStartState());
			}
		}

		return result;
	}
	
	@Override
	public RegularExpression withCondition(Condition condition) {
		return (RegularExpression) super.withCondition(condition);
	}
	
}
