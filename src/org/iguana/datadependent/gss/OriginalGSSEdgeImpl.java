package org.iguana.datadependent.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;

public class OriginalGSSEdgeImpl extends org.iguana.parser.gss.OriginalGSSEdgeImpl {
	
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
		
		Environment env = this.env;
		
		if (returnSlot.requiresBinding())
			env = returnSlot.doBinding(sppfNode, env);
		
		parser.setEnvironment(env);
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, parser.getEvaluatorContext()))
			return null;
		
		env = parser.getEnvironment();
		
		NonPackedNode y;
		
		if (returnSlot.isLast() && !returnSlot.isEnd()) {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), env);
			
			if (parser.getCurrentEndGrammarSlot().isEnd()) {
				if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) {
					org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
					y = parser.getNode(returnSlot, getNode(), sppfNode, env, dest.getData()); // use the original slot to create a node
				} else {
					y = parser.getNode(returnSlot, getNode(), sppfNode, env, null); // use the original slot to create a node
				}
				returnSlot = parser.getCurrentEndGrammarSlot();
				env = parser.getEnvironment();
			} else
				return null;
		} else {
			if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) {
				org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
				y = parser.getNode(returnSlot, getNode(), sppfNode, env, dest.getData());
			} else {
				y = parser.getNode(returnSlot, getNode(), sppfNode, env, null);
			}
		}
		
		if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
			return new org.iguana.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
		
		return null;
	}

}
