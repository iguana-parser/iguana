package org.jgll.parser;

import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.Level;

/**
 * 
 * The values needed to uniquely identify a GSS edge are
 * the Source GSS node's grammar label, input index,
 * SPPF node's grammar label, and the Destination GSS node's
 * grammar label and input index.
 * 
 * Note that the SPPF's left extent is equal to the destination node's
 * input index and its right extent is equal to the source node's
 * input index. However, since Dummy nodes don't have input
 * indices, we use the input indices from GSS nodes to 
 * uniquely identify a GSS edge.
 * 
 * src (L1, j)
 * dst (L2, i)
 * node (L, i, j)
 * 
 * @author Ali Afroozeh
 * 
 */
public class GSSEdge implements Level {
	
	public static final ExternalHasher<GSSEdge> externalHasher = new GSSEdgeExternalHasher();
	public static final ExternalHasher<GSSEdge> levelBasedExternalHasher = new GSSEdgeExternalHasher();

	private final GSSNode src;
	private final SPPFNode sppfNode;
	private final GSSNode dst;
	
	public GSSEdge(GSSNode src, SPPFNode sppfNode, GSSNode dst) {
		this.src = src;
		this.sppfNode = sppfNode;
		this.dst = dst;	
	}		
	
	public SPPFNode getSppfNode() {
		return sppfNode;
	}
	
	public GSSNode getDestination() {
		return dst;
	}
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(this == o) {
			return true;
		}
		
		if(! (o instanceof GSSEdge)) {
			return false;
		}
		
		GSSEdge other = (GSSEdge) o;
		
		
		return src.getGrammarSlot() == other.src.getGrammarSlot() &&
			   src.getInputIndex() == other.src.getInputIndex() &&
			   dst.getGrammarSlot() == other.dst.getGrammarSlot() &&
			   dst.getInputIndex() == other.dst.getInputIndex() &&
			   compare(sppfNode, other.sppfNode);
	}
	
	private boolean compare(SPPFNode first, SPPFNode second) {
		if(first instanceof TerminalSymbolNode && second instanceof TerminalSymbolNode) {
			return ((TerminalSymbolNode) first).getMatchedChar() == ((TerminalSymbolNode) second).getMatchedChar(); 
		} 
		else {
			return first.getGrammarSlot() == second.getGrammarSlot();
		}
	}
	
	@Override
	public int getLevel() {
		return src.getInputIndex();
	}
	
	public static class GSSEdgeExternalHasher implements ExternalHasher<GSSEdge> {

		private static final long serialVersionUID = 1L;

		/**
		 * Currently terminal nodes do not have a grammar slot. As a result,
		 * we cannot use their grammar slot to calculate their hashcode or equality.
		 * We use the matched character of terminal nodes for hashcode and equality.
		 * The problem is that matched characters may clash with the id of grammar slots.
		 * To avoid such clashes, we use to arbitrary numbers (31, 17) to distinguish the
		 * type of nodes.
		 * 
		 * May change in the future.
		 */
		
		@Override
		public int hash(GSSEdge edge, HashFunction f) {
			if(edge.sppfNode instanceof TerminalSymbolNode) {
				return f.hash(edge.src.getGrammarSlot().getId(),
						   edge.src.getInputIndex(),
						   edge.dst.getGrammarSlot().getId(),
						   edge.dst.getInputIndex(),
						   31,
						   ((TerminalSymbolNode) edge.sppfNode).getMatchedChar());
			} 
			else {
				return f.hash(edge.src.getGrammarSlot().getId(),
						   edge.src.getInputIndex(),
						   edge.dst.getGrammarSlot().getId(),
						   edge.dst.getInputIndex(),
						   17,
						   edge.sppfNode.getGrammarSlot().getId());
			}
		}
		
	}
	
	public static class GSSEdgeLevelBasedExternalHasher implements ExternalHasher<GSSEdge> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSEdge edge, HashFunction f) {
			if(edge.sppfNode instanceof TerminalSymbolNode) {
				return f.hash(edge.src.getGrammarSlot().getId(),
						   edge.dst.getGrammarSlot().getId(),
						   edge.dst.getInputIndex(),
						   31,
						   ((TerminalSymbolNode) edge.sppfNode).getMatchedChar());
			} 
			else {
				return f.hash(edge.src.getGrammarSlot().getId(),
						   edge.dst.getGrammarSlot().getId(),
						   edge.dst.getInputIndex(),
						   17,
						   edge.sppfNode.getGrammarSlot().getId());
			}
		}
		
	}

	
}
