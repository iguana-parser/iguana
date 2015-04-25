package org.jgll.grammar.slot;

import org.jgll.datadependent.ast.Statement;
import org.jgll.datadependent.env.Environment;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.generator.GeneratorUtil;

public class CodeTransition extends AbstractTransition {
	
	private final Statement[] statements;

	public CodeTransition(Statement[] statements, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.statements = statements;
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		parser.evaluate(statements, parser.getEmptyEnvironment());
		
		if (parser.getEnvironment().isEmpty()) {
			dest.execute(parser, u, i, node);
		} else {
			dest.execute(parser, u, i, node, parser.getEnvironment());
		}
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		parser.evaluate(statements, env);
		dest.execute(parser, u, i, node, parser.getEnvironment());
	}

	@Override
	public String getLabel() {
		return GeneratorUtil.listToString(statements, ";");
	}
	
}
