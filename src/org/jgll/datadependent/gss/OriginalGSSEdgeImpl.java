package org.jgll.datadependent.gss;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

public class OriginalGSSEdgeImpl extends org.jgll.parser.gss.OriginalGSSEdgeImpl {
	
	private final Environment env;

	public OriginalGSSEdgeImpl(NonPackedNode node, GSSNode destination, Environment env) {
		super(node, destination);
		
		assert env != null;
		this.env = env;
	}
	
	@Override
	public Descriptor addDescriptor(GLLParser parser, GSSNode source, int inputIndex, NonPackedNode sppfNode) {
		
		GSSNode destination = getDestination();
		BodyGrammarSlot returnSlot = (BodyGrammarSlot) source.getGrammarSlot();
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, env))
			return null;
		
		Environment env = this.env;
		
		if (returnSlot.requiresBinding()) {
			env = returnSlot.doBinding(sppfNode, env);
		}
		
		// FIXME: SPPF
		NonPackedNode y = parser.getNode(returnSlot, getNode(), sppfNode);
		
		if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env)) {
			return new org.jgll.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
		}
		
		return null;
	}

}
