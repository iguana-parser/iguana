package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected final Automaton automaton;

	public AbstractRegularExpression(String name) {
		this(name, Collections.<Condition>emptySet());
	}
	
	public AbstractRegularExpression(String name, Set<Condition> conditions) {
		super(name, conditions);
		this.automaton = combineConditions(createAutomaton());
	}
	
	@Override
	public Automaton toAutomaton() {
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
	public RegularExpression withCondition(Condition condition) {
		return (RegularExpression) super.withCondition(condition);
	}
	
}
