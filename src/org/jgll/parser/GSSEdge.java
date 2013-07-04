package org.jgll.parser;

import org.jgll.sppf.SPPFNode;

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
 */
public class GSSEdge {

	private final SPPFNode sppfNode;
	private final GSSNode dst;
	
	private final int hash;
	
	public GSSEdge(SPPFNode sppfNode, GSSNode dst) {
		this.sppfNode = sppfNode;
		this.dst = dst;	
		
		hash = HashFunctions.defaulFunction().hash(dst.hashCode(),
												   sppfNode.hashCode());
	}
	
	public SPPFNode getSppfNode() {
		return sppfNode;
	}
	
	public GSSNode getDestination() {
		return dst;
	}
	
	@Override
	public int hashCode() {
		return hash;
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
		
		return hash == other.hash &&
				dst.equals(other.dst) &&
			   sppfNode.equals(other.sppfNode);
	}
	
}
