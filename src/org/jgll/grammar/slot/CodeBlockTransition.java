package org.jgll.grammar.slot;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class CodeBlockTransition extends AbstractTransition {
	
	@SuppressWarnings("unused")
	private final CodeBlock code;

	public CodeBlockTransition(CodeBlock code, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
		this.code = code;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		// TODO
		return;
	}

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return null;
	}

}
