package org.jgll.datadependent.descriptor;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class Descriptor extends org.jgll.parser.descriptor.Descriptor {
	
	private final Environment env;
	
	public Descriptor(BodyGrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode, Environment env) {
		super(slot, gssNode, inputIndex, sppfNode);
		
		assert env != null;
		this.env = env;
	}

	@Override
	public void execute(GLLParser parser) {
		getGrammarSlot().execute(parser, getGSSNode(), getInputIndex(), getSPPFNode(), env);
	}
}
