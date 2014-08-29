package org.jgll.grammar;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.test.ConditionTest;

public interface Conditions {
	
	public ConditionTest getPostConditions(Set<Condition> conditions);
	
	public ConditionTest getPreConditions(Set<Condition> conditions);

}
