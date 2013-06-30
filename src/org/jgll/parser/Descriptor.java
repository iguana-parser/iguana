package org.jgll.parser;

import org.jgll.grammar.GrammarSlot;
import org.jgll.sppf.SPPFNode;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * A {@code Descriptor} is used by the GLL parser to keep track of the 
 * nonterminals that have been encountered during the parsing process but that
 * have not been processed yet.
 * <br />
 * A descriptor is a 4-tuple that contains a {@code label} that is used to
 * indicate the code that has to be executed to parse the encountered 
 * nonterminal, a {@code gssNode} which represents the current top in the 
 * Graph Structured Stack, an {@code index}, which is the current location in 
 * the input string and an {@code sppfNode} which is the SPPF node that was
 * created for the nonterminal.
 * 
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Maarten Manders
 * @author Ali Afroozeh
 * 
 */

public class Descriptor {
	
	/**
	 * The label that indicates the parser code to execute for the encountered
	 * nonterminal.
	 */
	private final GrammarSlot slot;
	
	/**
	 * The associated GSSNode.
	 */
	private final GSSNode gssNode;
	
	/**
	 * The current index in the input string.
	 */
	private final int inputIndex;
	
	/**
	 * The SPPF node that was created before parsing the encountered 
	 * nonterminal.
	 */
	private final SPPFNode sppfNode;
	
	private final int hash;
	
	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode) {
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		assert sppfNode != null;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
		this.sppfNode = sppfNode;
		
		HashFunction hf = Hashing.murmur3_32();
		HashCode code = hf.newHasher()
						  .putInt(slot.getId())
						  .putInt(inputIndex)
						  .putInt(sppfNode.getGrammarSlot().getId())
						  .putInt(sppfNode.getLeftExtent())
						  .putInt(gssNode.getGrammarSlot().getId()).hash();
		
		hash = code.asInt();
	}
	
	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public GSSNode getGSSNode() {
		return gssNode;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}

	public SPPFNode getSPPFNode() {
		return sppfNode;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) { 
			return true;
		}
		
		if(! (obj instanceof Descriptor)) {
			return false;
		}
		
		Descriptor other = (Descriptor) obj;
		
		/**
		 *	Descriptors are of the form (L,u,i,w) where u is a GGS node and w is an
		 *	SPPF node. As w has the form
		 *	(L1,j,i) and u has the form (L2,j), in fact a descriptor could be
		 *	presented as (L, i, j, L2, L1).
		 */
		return hash == other.hash &&
			   slot == other.slot &&
			   inputIndex == other.getInputIndex() &&
			   sppfNode.getGrammarSlot() == other.sppfNode.getGrammarSlot() &&
			   sppfNode.getLeftExtent() == other.sppfNode.getLeftExtent() &&	
			   gssNode.getGrammarSlot() == other.gssNode.getGrammarSlot();
	}
	
	@Override
	public String toString() {
		return "(" + slot + ", " + inputIndex + ", " + gssNode.getGrammarSlot() + ", " + sppfNode + ")";
	}
	
}
