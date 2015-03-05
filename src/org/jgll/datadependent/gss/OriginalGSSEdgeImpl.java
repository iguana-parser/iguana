package org.jgll.datadependent.gss;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DummySlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.DummyNode;
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
		
		parser.setEnvironment(env);
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, parser.getEvaluatorContext()))
			return null;
		
		Environment env = parser.getEnvironment();
		
		if (returnSlot.requiresBinding())
			env = returnSlot.doBinding(sppfNode, env);
		
		NonPackedNode y;
		
		if (returnSlot.isLast() && !returnSlot.isEnd()) {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), env);
			
			if (parser.getCurrentEndGrammarSlot().isEnd()) {
				y = parser.getNode(returnSlot, getNode(), sppfNode); // use the original slot to create a node
				returnSlot = parser.getCurrentEndGrammarSlot();
				env = parser.getEnvironment();
			} else
				return null;
		} else
			y = parser.getNode(returnSlot, getNode(), sppfNode);
		
		if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
			return new org.jgll.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
		
		return null;
	}

}
