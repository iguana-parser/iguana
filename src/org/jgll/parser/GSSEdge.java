package org.jgll.parser;

import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.Decomposer;
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
		return HashFunctions.defaulFunction().hash(src.getGrammarSlot().getId(),
											       src.getInputIndex(),
												   dst.getGrammarSlot().getId(),
												   dst.getInputIndex(),
												   sppfNode.hashCode());
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
			   sppfNode.equals(other.sppfNode);
	}

	@Override
	public int getLevel() {
		return src.getInputIndex();
	}
	
	public static class GSSEdgeDecomposer implements Decomposer<GSSEdge> {

		private int[] components = new int[5];
		
		@Override
		public int[] toIntArray(GSSEdge gssEdge) {
			components[0] = gssEdge.src.getGrammarSlot().getId();
			components[1] = gssEdge.src.getInputIndex();
			components[2] = gssEdge.dst.getGrammarSlot().getId();
			components[3] = gssEdge.dst.getInputIndex();
			components[4] = gssEdge.sppfNode.hashCode();
			return components;
		}
		
	}
	
}
