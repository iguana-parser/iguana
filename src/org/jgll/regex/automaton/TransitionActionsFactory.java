package org.jgll.regex.automaton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;

public class TransitionActionsFactory {

	public static TransitionAction getPostActions(Set<Condition> conditions) {
		
		if (conditions.size() == 0) {
			return null;
		}
		
		final List<RegularExpression> notFollows = new ArrayList<>();
		
		for(Condition condition : conditions) {
			
			switch (condition.getType()) {
			
//			case FOLLOW:
//				if (condition instanceof RegularExpressionCondition) {
//					postConditionActions.add(FollowActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
//				} 
//				else {
////					postConditions.addCondition(convertCondition((ContextFreeCondition) condition), condition);
//				}
//				break;
				
			case NOT_FOLLOW:
				if (condition instanceof RegularExpressionCondition) {
					RegularExpression regex = ((RegularExpressionCondition) condition).getRegularExpression();
					notFollows.add(regex);
				} 
				else {
				}
				break;
				
			
			default:
				break;
			}
		}
		
		return new TransitionAction() {

			RegularExpression regex = notFollows.size() == 1 ? notFollows.get(0) : new RegexAlt<>(notFollows);
			
			RunnableAutomaton r = regex.toAutomaton().getRunnableAutomaton();
			
			@Override
			public boolean execute(Input input, int index) {
				// Run from the next input index
				if (index + 1 > input.length() - 1)  return false;
				return r.match(input, index + 1) > 0;
			}
			
			@Override
			public String toString() {
				return "!>> " + regex.toString();
			}
		};
	}
	
}
