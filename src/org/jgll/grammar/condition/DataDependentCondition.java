package org.jgll.grammar.condition;

import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.env.persistent.PersistentEvaluatorContext;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class DataDependentCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private final org.jgll.datadependent.ast.Expression expression;
	
	private transient final SlotAction action;

	DataDependentCondition(ConditionType type, org.jgll.datadependent.ast.Expression expression) {
		super(type);
		this.expression = expression;
		this.action = new SlotAction() {
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex) {
				return execute(input, gssNode, inputIndex, new PersistentEvaluatorContext());
			}
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex, IEvaluatorContext ctx) {
				Object value = expression.interpret(ctx);
				if (!(value instanceof Boolean)) 
					throw new RuntimeException("Data dependent condition should evaluate to a boolean value."); 
				return (!(Boolean) value);
			}
			
		};
	}
	
	public org.jgll.datadependent.ast.Expression getExpression() {
		return expression;
	}
	
	@Override
	public boolean isDataDependent() {
		return true;
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public SlotAction getSlotAction() {
		return action;
	}
	
	static public DataDependentCondition predicate(org.jgll.datadependent.ast.Expression expression) {
		return new DataDependentCondition(ConditionType.DATA_DEPENDENT, expression);
	}

}
