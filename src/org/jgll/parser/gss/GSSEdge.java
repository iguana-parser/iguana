package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class GSSEdge {

	public static final ExternalHasher<GSSEdge> externalHasher = new GSSEdgeExternalHasher();

	private BodyGrammarSlot returnSlot;
	private SPPFNode node;
	private GSSNode destination;

	private final int hash;

	public GSSEdge(BodyGrammarSlot slot, SPPFNode node, GSSNode destination) {
		this.returnSlot = slot;
		this.node = node;
		this.destination = destination;
		this.hash = externalHasher.hash(this, HashFunctions.defaulFunction());
	}

	public SPPFNode getNode() {
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

		int slotId1 = 0;
		if (node instanceof TokenSymbolNode) {
			slotId1 = node.getLeftExtent();
		} else {
			slotId1 = node.getLeftExtent();
		}

		int slotId2 = 0;
		if (node instanceof TokenSymbolNode) {
			slotId2 = other.node.getLeftExtent();
		} else {
			slotId2 = other.node.getId();
		}

		// Because destination.getInputIndex() == node.getLeftExtent, we don't
		// use it here.
		return returnSlot == other.returnSlot
				&& slotId1 == slotId2
				&& node.getLeftExtent() == other.node.getLeftExtent()
				&& node.getRightExtent() == other.node.getRightExtent()
				&& destination.getGrammarSlot() == other.destination
						.getGrammarSlot();
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public static class GSSEdgeExternalHasher implements
			ExternalHasher<GSSEdge> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSEdge edge, HashFunction f) {

			int slotId = 0;
			if (edge.node instanceof TokenSymbolNode) {
				slotId = edge.node.getLeftExtent();
			} else {
				slotId = edge.node.getId();
			}

			return f.hash(edge.returnSlot.getId(), slotId,
					edge.node.getLeftExtent(), edge.node.getRightExtent(), edge
							.getDestination().getGrammarSlot().getId());
		}

		@Override
		public boolean equals(GSSEdge e1, GSSEdge e2) {

			int slotId1 = 0;
			if (e1.node instanceof TokenSymbolNode) {
				slotId1 = e1.node.getLeftExtent();
			} else {
				slotId1 = e1.node.getId();
			}

			int slotId2 = 0;
			if (e2.node instanceof TokenSymbolNode) {
				slotId2 = e2.node.getLeftExtent();
			} else {
				slotId2 = e2.node.getId();
			}

			return e1.returnSlot.getId() == e2.returnSlot.getId()
					&& slotId1 == slotId2
					&& e1.node.getLeftExtent() == e2.node.getLeftExtent()
					&& e1.node.getRightExtent() == e2.node.getRightExtent()
					&& e1.destination.getGrammarSlot() == e2.destination
							.getGrammarSlot();
		}
	}
}
