package org.iguana.datadependent.descriptor;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

public class Descriptor extends org.iguana.parser.descriptor.Descriptor {
	
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
