package org.jgll.grammar.slot;

import org.jgll.datadependent.ast.Expression;
import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.exception.UnexpectedRuntimeTypeException;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

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

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		Object value = parser.evaluate(condition, parser.getEmptyEnvironment());
		
		if (!(value instanceof Boolean)) {
			throw new UnexpectedRuntimeTypeException(condition);
		}
		
		boolean isTrue = ((Boolean) value) == true;
		
		if (isTrue) {
			dest.execute(parser, u, i, node);
		} else {
			ifFalse.execute(parser, u, i, node);
		}
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
		
		if (isTrue) {
			dest.execute(parser, u, i, node, env);
		} else {
			ifFalse.execute(parser, u, i, node, env);
		}
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

}
