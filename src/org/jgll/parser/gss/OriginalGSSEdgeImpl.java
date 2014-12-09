package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.SPPFNode;

public class OriginalGSSEdgeImpl implements GSSEdge {
	
	private SPPFNode node;
	private GSSNode destination;

	public OriginalGSSEdgeImpl(SPPFNode node, GSSNode destination) {
		this.node = node;
		this.destination = destination;
	}

	public SPPFNode getNode() {
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
		
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof GSSEdge)) {
			return false;
		}

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

}
