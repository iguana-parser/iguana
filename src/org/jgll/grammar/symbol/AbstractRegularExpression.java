package org.jgll.grammar.symbol;

import java.util.regex.Pattern;

import org.jgll.regex.Matcher;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.automaton.Automaton;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected Automaton automaton;
	
	protected Pattern pattern;
	
	public AbstractRegularExpression(SymbolBuilder<? extends RegularExpression> builder) {
		super(builder);
	}
	
	@Override
	public Matcher getDFAMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().match(input, i);
	}
	
	@Override
	public Matcher getBackwardsMatcher() {
		return (input, i) -> getAutomaton().getRunnableAutomaton().matchBackwards(input, i);
	}
	
	@Override
	public Matcher getJavaRegexMatcher() {
		
		if (pattern == null) {
			pattern = Pattern.compile("");
		}
		
		return (input, i) -> {
								java.util.regex.Matcher matcher = pattern.matcher("");
								matcher.find(i);
								return matcher.end();
							 };
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
