package org.jgll.parser;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;


public class GSSEdge {
	
	public static final ExternalHasher<GSSEdge> externalHasher = new GSSEdgeExternalHasher();
	
	private BodyGrammarSlot slot;
	private SPPFNode node;

	public GSSEdge(BodyGrammarSlot slot, SPPFNode node) {
		this.slot = slot;
		this.node = node;
	}
	
	public SPPFNode getNode() {
		return node;
	}
	
	public BodyGrammarSlot getSlot() {
		return slot;
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

		return  slot == other.slot &&
				node.getGrammarSlot() == other.node.getGrammarSlot() &&
				node.getLeftExtent() == other.node.getLeftExtent() &&
				node.getRightExtent() == other.node.getRightExtent();
	}

	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	public static class GSSEdgeExternalHasher implements ExternalHasher<GSSEdge> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSEdge edge, HashFunction f) {
			return f.hash(edge.slot.getId(), 
						  edge.node.getGrammarSlot().getId(),
						  edge.node.getLeftExtent(),
						  edge.node.getRightExtent());
		}

		@Override
		public boolean equals(GSSEdge e1, GSSEdge e2) {
			return e1.slot.getId() == e2.slot.getId() &&
				   e1.node.getGrammarSlot().getId() == e2.node.getGrammarSlot().getId() &&
				   e1.node.getLeftExtent() == e2.node.getLeftExtent() &&
				   e1.node.getRightExtent() == e2.node.getRightExtent();
		}
	}
}
