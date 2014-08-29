package org.jgll;

import java.util.Set;

import org.jgll.grammar.Conditions;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;

public class FalseConditions implements Conditions {

	@Override
	public ConditionTest getPostConditions(Set<Condition> conditions) {
		return new FalseConditionTest();
	}

	@Override
	public ConditionTest getPreConditions(Set<Condition> conditions) {
		return new FalseConditionTest();
	}

}
