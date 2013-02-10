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

	private final GSSNode src;
	private final SPPFNode sppfNode;
	private final GSSNode dst;
	
	public GSSEdge(GSSNode source, SPPFNode sppfNode, GSSNode dst) {
		this.src = source;
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
		int result = 17;
		result += 31 * result + src.hashCode();
		result += 31 * result + sppfNode.hashCode();
		result += 31 * result + dst.hashCode();
		return result;
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
		
		return src.equals(other.src) &&
			   sppfNode.equals(other.sppfNode) &&
			   dst.equals(other.dst);
	}
	
}
