package org.jgll.parser.gss;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.DummySlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;

public class NewGSSEdgeImpl implements GSSEdge {
	
	private final BodyGrammarSlot returnSlot;
	private final NonPackedNode node;
	private final GSSNode destination;

	public NewGSSEdgeImpl(BodyGrammarSlot slot, NonPackedNode node, GSSNode destination) {
		this.returnSlot = slot;
		this.node = node;
		this.destination = destination;
	}

	public NonPackedNode getNode() {
		return node;
	}

	public BodyGrammarSlot getReturnSlot() {
		return returnSlot;
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
		// node.getRightExtent() == source.getLeftExtent we don't use them here.
		return 	returnSlot == other.getReturnSlot()
				&& destination.getInputIndex() == other.getDestination().getInputIndex()
				&& destination.getGrammarSlot() == other.getDestination().getGrammarSlot();
	}

	@Override
	public int hashCode() {
		return  HashFunctions.defaulFunction.hash(returnSlot.hashCode(), 
												  destination.getInputIndex(), 
												  destination.getGrammarSlot().hashCode());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", returnSlot, node, destination);
	}

	@Override
	public Descriptor addDescriptor(GLLParser parser, GSSNode source, int inputIndex, NonPackedNode sppfNode) {
		
		/**
		 * 
		 * Data-dependent GLL parsing
		 * 
		 */
		
		NonPackedNode y;
		BodyGrammarSlot returnSlot = this.returnSlot;
		
		if (returnSlot.requiresBinding()) {
			Environment env = returnSlot.doBinding(sppfNode, parser.getEmptyEnvironment());
			
			parser.setEnvironment(env);
			
			if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, parser.getEvaluatorContext()))
				return null;
			
			env = parser.getEnvironment();
			
			if (returnSlot.isLast() && !returnSlot.isEnd()) {
				parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
				returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), parser.getEnvironment());
				
				if (parser.getCurrentEndGrammarSlot().isEnd()) {
					if (destination instanceof org.jgll.datadependent.gss.GSSNode<?>) {
						org.jgll.datadependent.gss.GSSNode<?> dest = (org.jgll.datadependent.gss.GSSNode<?>) destination;
						y = parser.getNode(returnSlot, node, sppfNode, env, dest.getData()); // use the original slot to create a node
					} else {
						y = parser.getNode(returnSlot, node, sppfNode, env, null); // use the original slot to create a node
					}
					returnSlot = parser.getCurrentEndGrammarSlot();
					env = parser.getEnvironment();
				} else 
					return null;
			} else {
				if (destination instanceof org.jgll.datadependent.gss.GSSNode<?>) {
					org.jgll.datadependent.gss.GSSNode<?> dest = (org.jgll.datadependent.gss.GSSNode<?>) destination;
					y = parser.getNode(returnSlot, node, sppfNode, env, dest.getData());
				} else {
					y = parser.getNode(returnSlot, node, sppfNode, env, null);
				}
			}
				
			if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
				return new org.jgll.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
				
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
