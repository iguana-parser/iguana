package org.iguana.grammar.slot;

import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.env.Environment;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.generator.GeneratorUtil;

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
