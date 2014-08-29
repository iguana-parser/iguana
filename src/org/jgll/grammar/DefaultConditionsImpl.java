package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.DefaultConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.slotaction.FollowActions;
import org.jgll.grammar.slotaction.LineActions;
import org.jgll.grammar.slotaction.NotFollowActions;
import org.jgll.grammar.slotaction.NotMatchActions;
import org.jgll.grammar.slotaction.NotPrecedeActions;
import org.jgll.grammar.slotaction.PrecedeActions;
import org.jgll.grammar.slotaction.SlotAction;

public class DefaultConditionsImpl implements Conditions {
	
	@Override
	public ConditionTest getPostConditions(final Set<Condition> conditions) {
		
		List<SlotAction<Boolean>> postConditionActions = new ArrayList<>();
		
		for (Condition condition : conditions) {
			
			switch (condition.getType()) {
				
				case FOLLOW:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(FollowActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
					} 
					else {
//						postConditions.addCondition(convertCondition((ContextFreeCondition) condition), condition);
					}
					break;
					
				case NOT_FOLLOW:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(NotFollowActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
					} 
					else {
					}
					break;
					
				case MATCH:
					break;
						
				case NOT_MATCH:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(NotMatchActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
					} else {
						
					}
					break;

				case END_OF_LINE:
					postConditionActions.add(LineActions.addEndOfLine(condition));
				  break;
				  
				default:
					break;
			}
		}
		
		ConditionTest postCondition;
		
		if(postConditionActions.size() > 0) {
			postCondition = new DefaultConditionTest(postConditionActions);
		} else {
			postCondition = new FalseConditionTest();
		}

		return postCondition;
	}
	
	@Override
	public ConditionTest getPreConditions(final Set<Condition> conditions) {

		List<SlotAction<Boolean>> preConditionActions = new ArrayList<>();
		
		for (Condition condition : conditions) {
			switch (condition.getType()) {

				case PRECEDE:
					assert !(condition instanceof ContextFreeCondition);
					
					if(condition instanceof RegularExpressionCondition) {
						preConditionActions.add(PrecedeActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
					} 

					break;
					
				case NOT_PRECEDE:
					assert !(condition instanceof ContextFreeCondition);
					
					if(condition instanceof RegularExpressionCondition) {
						preConditionActions.add(NotPrecedeActions.fromRegularExpression(((RegularExpressionCondition) condition).getRegularExpression(), condition));
					} 
					break;
					
				case START_OF_LINE:
					preConditionActions.add(LineActions.addStartOfLine(condition));
				  break;
				  
				default:
					break;
			}
		}

		ConditionTest preCondition;
		
		if(preConditionActions.size() > 0) {
			preCondition = new DefaultConditionTest(preConditionActions);
		} else {
			preCondition = new FalseConditionTest();
		}
		
		return preCondition;
	}

}
