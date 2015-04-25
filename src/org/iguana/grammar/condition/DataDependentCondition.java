package org.iguana.grammar.condition;

import org.iguana.datadependent.env.IEvaluatorContext;
import org.iguana.parser.gss.GSSNode;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.util.Input;

public class DataDependentCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private final org.iguana.datadependent.ast.Expression expression;
	
	private transient final SlotAction action;

	DataDependentCondition(ConditionType type, org.iguana.datadependent.ast.Expression expression) {
		super(type);
		this.expression = expression;
		this.action = new SlotAction() {
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex) {
				throw new UnsupportedOperationException();
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
	
	public org.iguana.datadependent.ast.Expression getExpression() {
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
	
	static public DataDependentCondition predicate(org.iguana.datadependent.ast.Expression expression) {
		return new DataDependentCondition(ConditionType.DATA_DEPENDENT, expression);
	}
	
	@Override
	public String toString() {
		return String.format("[%s]", expression);
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
