package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.regex.automaton.StateAction;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected List<StateAction> actions;

	public AbstractRegularExpression(String name) {
		super(name);
		actions = new ArrayList<>();
	}
	
	@Override
	public void addFinalStateAction(StateAction action) {
		actions.add(action);
	}
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions) {
		return (RegularExpression) super.addConditions(conditions);
	}

	protected Automaton combineConditions(Automaton a) {

		Automaton union = null;
		
		for(Condition condition : conditions) {
			if(condition.getType() == ConditionType.NOT_MATCH && condition instanceof RegularExpressionCondition) {
				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
				RegularExpression regex = regexCondition.getRegularExpression();
				if(union == null) {
					union = regex.toAutomaton();
				} else {
					union = AutomatonOperations.union(union, regex.toAutomaton());
				}
				
			}
		}

		if(union == null) {
			return a;
		}
		
		return AutomatonOperations.difference(a, union);
	}
	
}
