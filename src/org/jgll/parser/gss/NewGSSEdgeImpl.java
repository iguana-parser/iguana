package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.NonPackedNode;

public class NewGSSEdgeImpl implements GSSEdge {
	
	private BodyGrammarSlot returnSlot;
	private NonPackedNode node;
	private GSSNode destination;

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

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof GSSEdge)) {
			return false;
		}

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

}
