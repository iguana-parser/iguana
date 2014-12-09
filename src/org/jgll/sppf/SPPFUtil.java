package org.jgll.sppf;

import org.jgll.util.hashing.hashfunction.HashFunction;

import static org.jgll.sppf.NonPackedNode.*;

/**
 *
 * 
 * @author Ali Afroozeh
 *
 */
public class SPPFUtil {

	private final Hash sppfHash;
	
	private final Equals sppfEquals;
	
	private static SPPFUtil instance;
	
	public static SPPFUtil getInstance() {
		if (instance == null) 
			throw new RuntimeException("Instance is not initialized yet.");
		return instance;
	}
	
	public static void initGlobal(HashFunction hashFunction) {
		init((node) -> hashFunction.hash(node.getGrammarSlot().hashCode(), node.getLeftExtent(), node.getRightExtent()),
		     (node, other) -> node.getGrammarSlot().equals(other.getGrammarSlot()) && node.getLeftExtent() == other.getLeftExtent() && node.getRightExtent() == other.getRightExtent());
	}
	
	public static void initDistributed(HashFunction hashFunction) {
		init((node) -> hashFunction.hash(node.getLeftExtent(), node.getRightExtent()),
		     (node, other) -> node.getLeftExtent() == other.getLeftExtent() && node.getRightExtent() == other.getRightExtent());
	}	
	
	public static void init(Hash hash, Equals equals) {
		instance = new SPPFUtil(hash, equals);
	}
	
	private SPPFUtil(Hash hash, Equals equals) {
		this.sppfHash = hash;
		this.sppfEquals = equals;
	}
	
	public int hash(SPPFNode node) { 
		return sppfHash.hash(node);
	}
	
	public boolean equals(SPPFNode node, SPPFNode other) {
		return sppfEquals.equals(node, other);
	}
	
}
