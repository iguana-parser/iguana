package org.iguana.parser.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.HashFunctions;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;

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
		
		/**
		 * 
		 * Data-dependent GLL parsing
		 * 
		 */
		NonPackedNode y;
		
		if (returnSlot.requiresBinding()) {
			Environment env =  returnSlot.doBinding(sppfNode, parser.getEmptyEnvironment());
			
			parser.setEnvironment(env);
			
			if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, parser.getEvaluatorContext()))
				return null;
			
			env = parser.getEnvironment();
			
			if (returnSlot.isLast() && !returnSlot.isEnd()) {
				parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
				returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), env);
				
				if (parser.getCurrentEndGrammarSlot().isEnd()) {
					if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) { // TODO: Ugly
						org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
						y = parser.getNode(returnSlot, node, sppfNode, env, dest.getData()); // use the original slot to create a node
					} else {
						y = parser.getNode(returnSlot, node, sppfNode, env, null); // use the original slot to create a node
					}
					returnSlot = parser.getCurrentEndGrammarSlot();
					env = parser.getEnvironment();
				} else {
					return null;
				}
			} else {
				if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) { // TODO: Ugly
					org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
					y = parser.getNode(returnSlot, node, sppfNode, env, dest.getData());
				} else {
					y = parser.getNode(returnSlot, node, sppfNode, env, null);
				}
			}
			
			if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
				return new org.iguana.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
			
			return null;
		}
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex))
			return null;
		
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
