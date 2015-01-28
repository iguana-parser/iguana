package org.jgll.grammar.slot;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class CodeBlockTransition extends AbstractTransition {
	
	private final CodeBlock code;

	public CodeBlockTransition(CodeBlock code, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.code = code;
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		parser.evaluate(code, parser.getEmptyEnvironment());
		
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
		parser.evaluate(code, env);
		dest.execute(parser, u, i, node, parser.getEnvironment());
	}

	@Override
	public GrammarSlot getSlot() {
		return null;
	}

}
