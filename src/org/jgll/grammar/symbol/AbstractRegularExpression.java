package org.jgll.grammar.symbol;

import static org.jgll.regex.automaton.AutomatonOperations.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	private Automaton automaton;

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
	
	@Override
	public Automaton toAutomaton() {
		if (automaton == null) {
			automaton = combineConditions(createAutomaton());
		}
		return automaton;
	}
	
	protected abstract Automaton createAutomaton();

	protected Automaton combineConditions(Automaton a) {
		
		Set<Automaton> notMatchSet = new HashSet<>();
		
		for(Condition condition : conditions) {
			if(condition.getType() == ConditionType.NOT_MATCH && condition instanceof RegularExpressionCondition) {
				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
				RegularExpression regex = regexCondition.getRegularExpression();
				notMatchSet.add(regex.toAutomaton());
			}
		}

		if(notMatchSet.size() == 0) return a;
		
		System.out.println("Fuck! " + notMatchSet.size());
		
		return AutomatonOperations.difference(a, union(notMatchSet));
	}
	
}
