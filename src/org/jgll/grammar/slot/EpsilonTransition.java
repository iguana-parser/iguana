package org.jgll.grammar.slot;

import org.jgll.datadependent.env.Environment;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class EpsilonTransition extends AbstractTransition {

	public EpsilonTransition(BodyGrammarSlot origin, BodyGrammarSlot dest) {
		super(origin, dest);
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		dest.execute(parser, u, i, node);
	}

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		dest.execute(parser, u, i, node, env);
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

}
