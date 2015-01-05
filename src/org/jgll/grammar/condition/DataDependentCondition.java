package org.jgll.grammar.condition;

import org.jgll.grammar.GrammarSlotRegistry;

public class DataDependentCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private final org.jgll.datadependent.ast.Expression expression;
	
	private transient final SlotAction action;

	DataDependentCondition(ConditionType type, org.jgll.datadependent.ast.Expression expression) {
		super(type);
		this.expression = expression;
		this.action = null; // TODO 
	}
	
	public org.jgll.datadependent.ast.Expression getExpression() {
		return expression;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}

	@Override
	public SlotAction getSlotAction() {
		return null;
	}
	
	static public DataDependentCondition predicate(org.jgll.datadependent.ast.Expression expression) {
		return new DataDependentCondition(ConditionType.DATA_DEPENDENT, expression);
	}

}
