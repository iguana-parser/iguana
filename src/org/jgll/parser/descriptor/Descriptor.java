package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

/**
 * @author Ali Afroozeh
 * 
 */
// The label of SPPFNode is the same as the slot
public class Descriptor {
	
	// L
	private final GrammarSlot slot;
	
	// (L1, i)
	private final GSSNode gssNode;
	
	// j
	private final int inputIndex;
	
	// (L, i, j)
	private final NonPackedNode sppfNode;

	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode) {
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		assert sppfNode != null;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
		this.sppfNode = sppfNode;
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

	public NonPackedNode getSPPFNode() {
		return sppfNode;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %s, %s)", slot, inputIndex, gssNode, sppfNode);
	}
	
}