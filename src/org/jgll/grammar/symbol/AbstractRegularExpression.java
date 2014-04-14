package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private Automaton automaton;

	public AbstractRegularExpression(String name) {
		super(name);
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
				result = AutomatonOperations.difference(result, regex.toAutomaton());
			}			
		}

		return result;
	}
	
	@Override
	public AbstractRegularExpression clone() {
		return (AbstractRegularExpression) super.clone();
	}
	
	@Override
	public RegularExpression addCondition(Condition condition) {
		return (RegularExpression) super.addCondition(condition);
	}
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions) {
		return (RegularExpression) super.addConditions(conditions);
	}
	
}
