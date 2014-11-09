package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.condition.ContextFreeCondition;
import org.jgll.grammar.condition.PositionalCondition;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.DefaultConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.slotaction.EndOfLineAction;
import org.jgll.grammar.slotaction.RegularExpressionFollowAction;
import org.jgll.grammar.slotaction.RegularExpressionNotFollowAction;
import org.jgll.grammar.slotaction.RegularExpressionNotMatchAction;
import org.jgll.grammar.slotaction.RegularExpressionNotPrecedeAction;
import org.jgll.grammar.slotaction.RegularExpressionPrecedeAction;
import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.grammar.slotaction.StartOfLineAction;

public class DefaultConditionsImpl implements Conditions {
	
	@Override
	public ConditionTest getPostConditions(final Set<Condition> conditions) {
		
		List<SlotAction<Boolean>> postConditionActions = new ArrayList<>();
		
		for (Condition condition : conditions) {
			
			switch (condition.getType()) {
				
				case FOLLOW:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(new RegularExpressionFollowAction((RegularExpressionCondition) condition));
					} 
					else {
//						postConditions.addCondition(convertCondition((ContextFreeCondition) condition), condition);
					}
					break;
					
				case NOT_FOLLOW:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(new RegularExpressionNotFollowAction((RegularExpressionCondition) condition));
					} 
					else {
					}
					break;
					
				case MATCH:
					break;
						
				case NOT_MATCH:
					if (condition instanceof RegularExpressionCondition) {
						postConditionActions.add(new RegularExpressionNotMatchAction((RegularExpressionCondition) condition));
					} else {
						
					}
					break;

				case END_OF_LINE:
					postConditionActions.add(new EndOfLineAction((PositionalCondition) condition));
				  break;
				  
				default:
					break;
			}
		}
		
		ConditionTest postCondition;
		
		if(postConditionActions.size() > 0) {
			postCondition = new DefaultConditionTest(postConditionActions);
		} else {
			postCondition = FalseConditionTest.getInstance();
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
						preConditionActions.add(new RegularExpressionPrecedeAction((RegularExpressionCondition) condition));
					} 

					break;
					
				case NOT_PRECEDE:
					assert !(condition instanceof ContextFreeCondition);
					
					if(condition instanceof RegularExpressionCondition) {
						preConditionActions.add(new RegularExpressionNotPrecedeAction((RegularExpressionCondition) condition));
					} 
					break;
					
				case START_OF_LINE:
					preConditionActions.add(new StartOfLineAction((PositionalCondition) condition));
				  break;
				  
				default:
					break;
			}
		}

		ConditionTest preCondition;
		
		if(preConditionActions.size() > 0) {
			preCondition = new DefaultConditionTest(preConditionActions);
		} else {
			preCondition = FalseConditionTest.getInstance();
		}
		
		return preCondition;
	}

}
