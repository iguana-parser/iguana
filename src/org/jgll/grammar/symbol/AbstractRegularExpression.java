package org.jgll.grammar.symbol;

import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected Automaton automaton;
	
	public AbstractRegularExpression(SymbolBuilder<? extends RegularExpression> builder) {
		super(builder);
	}
	
	@Override
	public Matcher getMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().match(input, i);
	}
	
	@Override
	public Matcher getBackwardsMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().matchBackwards(input, i);
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

//	protected Automaton combineConditions(Automaton a) {
//		
//		Automaton result = a;
//		
//		for(Condition condition : conditions) {
//			if(condition.getType() == ConditionType.NOT_MATCH && condition instanceof RegularExpressionCondition) {
//				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
//				RegularExpression regex = regexCondition.getRegularExpression();
//				result = AutomatonOperations.difference(result, regex.getAutomaton());
//			} 
//			else if (condition.getType() == ConditionType.NOT_FOLLOW && condition instanceof RegularExpressionCondition) {
//				RegularExpressionCondition regexCondition = (RegularExpressionCondition) condition;
//				RegularExpression regex = regexCondition.getRegularExpression();
//				
//				result = AutomatonOperations.addCondition(result, regex.getAutomaton());
//			}
//		}
//
//		return result;
//	}
	
	@Override
	public String toString() {
		if (label != null) return label;
		return super.toString();
	}

}
