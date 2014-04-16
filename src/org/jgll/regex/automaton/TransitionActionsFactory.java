package org.jgll.regex.automaton;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.RegularExpressionCondition;

public class TransitionActionsFactory {

	public static Action getPostActions(final Set<Condition> conditions) {
		
		if (conditions.size() == 0) {
			return null;
		}
		
		Set<Condition> notFollowConditions = new HashSet<>();
		
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
					notFollowConditions.add(condition);
				} 
				else {
				}
				break;
				
			
			default:
				break;
			}
		}
		

		if (notFollowConditions.size() == 0) {
			return null;
		}
		
		return new NotFollowAction(notFollowConditions);

	}
	
}
