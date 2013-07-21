package org.jgll.parser;

import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.HashFunction;
import org.jgll.util.hashing.HashKey;
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
 */
public class GSSEdge implements Level, HashKey {

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
		return HashFunctions.defaulFunction().hash(src.hashCode(), sppfNode.hashCode(), dst.hashCode());
	}
	
	@Override
	public int hash(HashFunction f) {
		return f.hash(src.hashCode(), sppfNode.hashCode(), dst.hashCode());
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
			   dst.equals(other.dst) &&
			   sppfNode.equals(other.sppfNode);
	}

	@Override
	public int getLevel() {
		return src.getInputIndex();
	}
	
}
