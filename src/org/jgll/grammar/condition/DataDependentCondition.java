package org.jgll.grammar.condition;

import org.jgll.datadependent.env.Environment;
import org.jgll.datadependent.env.IEvaluatorContext;
import org.jgll.datadependent.env.persistent.PersistentEvaluatorContext;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class DataDependentCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private final org.jgll.datadependent.ast.Expression expression;
	
	@SuppressWarnings("unused")
	private transient final SlotAction action;

	DataDependentCondition(ConditionType type, org.jgll.datadependent.ast.Expression expression) {
		super(type);
		this.expression = expression;
		this.action = new SlotAction() {
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex) {
				Object value = expression.interpret(new PersistentEvaluatorContext());
				if (!(value instanceof Boolean)) 
					throw new RuntimeException("Data dependent condition should evaluate to a boolean value."); 
				return (!(Boolean) value);
			}
			
			@Override
			public boolean execute(Input input, GSSNode gssNode, int inputIndex, Environment env) {
				IEvaluatorContext ctx = new PersistentEvaluatorContext();
				ctx.setEnvironment(env);
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
	public String getConstructorCode() {
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
