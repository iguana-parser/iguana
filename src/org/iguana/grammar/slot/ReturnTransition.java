package org.iguana.grammar.slot;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

public class ReturnTransition extends AbstractTransition {
	
	private final Expression expression;

	public ReturnTransition(Expression expression, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.expression = expression;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
	   Object value = parser.evaluate(expression, parser.getEmptyEnvironment());
	   parser.setCurrentValue(value);
	   if (parser.getEnvironment().isEmpty()) 
		   dest.execute(parser, u, i, node);
	   else
		   dest.execute(parser, u, i, node, parser.getEnvironment());
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		Object value = parser.evaluate(expression, env);
		parser.setCurrentValue(value);
		dest.execute(parser, u, i, node, parser.getEnvironment());
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

}
