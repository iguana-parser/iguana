package org.jgll.parser.gss;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DummySlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;

public class OriginalGSSEdgeImpl implements GSSEdge {
	
	private NonPackedNode node;
	private GSSNode destination;

	public OriginalGSSEdgeImpl(NonPackedNode node, GSSNode destination) {
		this.node = node;
		this.destination = destination;
	}

	public NonPackedNode getNode() {
		return node;
	}

	@Override
	public BodyGrammarSlot getReturnSlot() {
		return null;
	}

	public GSSNode getDestination() {
		return destination;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;

		if (!(obj instanceof GSSEdge))
			return false;

		GSSEdge other = (GSSEdge) obj;

		// Because destination.getInputIndex() == node.getLeftExtent, and
		//         node.getRightExtent() == source.getLeftExtent 
		// we don't use them here.
		return 	destination.getInputIndex() == other.getDestination().getInputIndex()
				&& destination.getGrammarSlot() == other.getDestination().getGrammarSlot();
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(destination.getInputIndex(), destination.getGrammarSlot().hashCode());
	}

	@Override
	public Descriptor addDescriptor(GLLParser parser, GSSNode source, int inputIndex, NonPackedNode sppfNode) {
		
		BodyGrammarSlot returnSlot = (BodyGrammarSlot) source.getGrammarSlot();
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex)) {
			return null;
		}
		
		/**
		 * 
		 * Data-dependent GLL parsing
		 * 
		 */
		NonPackedNode y; // FIXME: SPPF
		
		if (returnSlot.requiresBinding()) {
			Environment env =  returnSlot.doBinding(sppfNode, parser.getEmptyEnvironment());
			
			if (returnSlot.isLast() && !returnSlot.isEnd()) {
				parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
				returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), env);
				
				if (parser.getCurrentEndGrammarSlot().isEnd()) {
					y = parser.getNode(returnSlot, node, sppfNode); // use the original slot to create a node
					returnSlot = parser.getCurrentEndGrammarSlot();
					env = parser.getEnvironment();
				} else
					return null;
			} else
				y = parser.getNode(returnSlot, node, sppfNode);
			
			if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
				return new org.jgll.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
			
			return null;
		}
		
		if (returnSlot.isLast() && !returnSlot.isEnd()) {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex));
			
			if (parser.getCurrentEndGrammarSlot().isEnd()) {
				y = parser.getNode(returnSlot, node, sppfNode); // use the original slot to create a node
				returnSlot = parser.getCurrentEndGrammarSlot();
			} else
				return null;
		} else
			y = parser.getNode(returnSlot, node, sppfNode);
		
		if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y)) {
			return new Descriptor(returnSlot, destination, inputIndex, y);
		}
		
		return null;
	}

}
