package org.jgll.parser;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
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
		
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof GSSEdge)) {
			return false;
		}
		
		GSSEdge other = (GSSEdge) obj;
		
		int slotId1 = 0;
		if(node instanceof TerminalSymbolNode) {
			slotId1 = ((TerminalSymbolNode) node).getInputIndex();
		} else {
			slotId1 = node.getGrammarSlot().getId();
		}

		int slotId2 = 0;
		if(other.node instanceof TerminalSymbolNode) {
			slotId2 = ((TerminalSymbolNode) other.node).getInputIndex();
		} else {
			slotId2 = other.node.getGrammarSlot().getId();
		}

		
		//	Because destination.getInputIndex() == node.getLeftExtent, we don't use it here.
		return  returnSlot == other.returnSlot &&
				slotId1 == slotId2 &&
				node.getLeftExtent() == other.node.getLeftExtent() &&
				node.getRightExtent() == other.node.getRightExtent() &&
				destination.getGrammarSlot() == other.destination.getGrammarSlot();
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	public static class GSSEdgeExternalHasher implements ExternalHasher<GSSEdge> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSEdge edge, HashFunction f) {
			
			int slotId = 0;
			if(edge.node instanceof TerminalSymbolNode) {
				slotId = ((TerminalSymbolNode) edge.node).getInputIndex();
			} else {
				slotId = edge.node.getGrammarSlot().getId();
			}
			
			return f.hash(edge.returnSlot.getId(), 
						  slotId,
						  edge.node.getLeftExtent(),
						  edge.node.getRightExtent(),
						  edge.getDestination().getGrammarSlot().getId());
		}

		@Override
		public boolean equals(GSSEdge e1, GSSEdge e2) {
			
			int slotId1 = 0;
			if(e1.node instanceof TerminalSymbolNode) {
				slotId1 = ((TerminalSymbolNode) e1.node).getInputIndex();
			} else {
				slotId1 = e1.node.getGrammarSlot().getId();
			}

			int slotId2 = 0;
			if(e2.node instanceof TerminalSymbolNode) {
				slotId2 = ((TerminalSymbolNode) e2.node).getInputIndex();
			} else {
				slotId2 = e2.node.getGrammarSlot().getId();
			}
			
			return e2.returnSlot.getId() == e2.returnSlot.getId() &&
				   slotId1 == slotId2 &&
				   e2.node.getLeftExtent() == e2.node.getLeftExtent() &&
				   e2.node.getRightExtent() == e2.node.getRightExtent();
		}
	}
}
