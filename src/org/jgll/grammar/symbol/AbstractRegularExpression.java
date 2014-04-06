package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateAction;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.Visualization;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected List<StateAction> actions;
	
	private Automaton automaton;

	public AbstractRegularExpression(String name) {
		super(name);
		actions = new ArrayList<>();
	}
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions) {
		return (RegularExpression) super.addConditions(conditions);
	}
	
	@Override
	public RegularExpression addCondition(Condition condition) {
		return (RegularExpression) super.addCondition(condition);
	}
	
	@Override
	public Automaton toAutomaton() {
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
				result = regex.toAutomaton();
			}
			
			if(condition.getType() == ConditionType.NOT_FOLLOW && condition instanceof RegularExpressionCondition) {
				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
				RegularExpression regex = regexCondition.getRegularExpression();
				
				Automaton c = regex.toAutomaton().determinize().copy();
				
				for(State s : a.getFinalStates()) {
					s.addTransition(Transition.emptyTransition(c.getStartState()));
				}
				
				for(State s : c.getFinalStates()) {
					s.setFinalState(false);
					s.setRejectState(true);
				}
				
				result = new Automaton(a.getStartState());
			}
		}

		return result;
	}
	
}
