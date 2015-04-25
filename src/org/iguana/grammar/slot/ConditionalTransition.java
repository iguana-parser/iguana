package org.iguana.grammar.slot;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.exception.UnexpectedRuntimeTypeException;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

public class ConditionalTransition extends AbstractTransition {
	
	private final Expression condition;
	
	private final BodyGrammarSlot ifFalse;

	public ConditionalTransition(Expression condition, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(condition, origin, dest, null);
	}
	
	public ConditionalTransition(Expression condition, BodyGrammarSlot origin, BodyGrammarSlot dest, BodyGrammarSlot ifFalse) {
		super(origin, dest);
		this.condition = condition;
		this.ifFalse = ifFalse;
	}

	public BodyGrammarSlot ifFalseDestination() {
		return ifFalse;
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		Object value = parser.evaluate(condition, parser.getEmptyEnvironment());
		
		if (!(value instanceof Boolean)) {
			throw new UnexpectedRuntimeTypeException(condition);
		}
		
		boolean isTrue = ((Boolean) value) == true;
		
		if (isTrue)
			dest.execute(parser, u, i, node);
		else if (ifFalse != null)
			ifFalse.execute(parser, u, i, node);
	}

	@Override
	public String getLabel() {
		return String.format("[%s]", condition.toString());
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		Object value = parser.evaluate(condition, env);
		
		if (!(value instanceof Boolean)) {
			throw new UnexpectedRuntimeTypeException(condition);
		}
		
		boolean isTrue = ((Boolean) value) == true;
		
		if (isTrue)
			dest.execute(parser, u, i, node, env);
		else if (ifFalse != null)
			ifFalse.execute(parser, u, i, node, env);
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

}
