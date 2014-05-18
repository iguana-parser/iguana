package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ConditionType;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.AutomatonOperations;
import org.jgll.util.CollectionsUtil;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected Automaton automaton;

	private Object object;
	
	private String label;

	public AbstractRegularExpression(String name) {
		this(name, null, Collections.<Condition>emptySet(), null);
	}
	
	public AbstractRegularExpression(String name, String label, Set<Condition> conditions, Object object) {
		super(name, conditions);
		this.label = label;
		this.object = object;
	}
	
	@Override
	public Automaton getAutomaton() {
		if (automaton == null) {
//			automaton = combineConditions(createAutomaton());
			automaton = createAutomaton();
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
				
				result = AutomatonOperations.addCondition(result, regex.getAutomaton());
			}
		}

		return result;
	}
	
	@Override
	public RegularExpression withCondition(Condition condition) {
		return (RegularExpression) super.withCondition(condition);
	}
	
	@Override
	public Object getObject() {
		return object;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return conditions.isEmpty() ? 
					label == null ? name : label + ":" + name
					: 
					"(" + label == null ? name : label + ":" + name + CollectionsUtil.listToString(conditions) + ")";
	}
	
}
