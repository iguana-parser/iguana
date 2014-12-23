package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;

/**
 * @author Ali Afroozeh
 * 
 */

public class Descriptor {
	
	private final GrammarSlot slot;
	
	private final GSSNode gssNode;
	
	private final int inputIndex;
	
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
	public int hashCode() {
		// The label of SPPFNode is the same as the slot
		return HashFunctions.defaulFunction.hash(slot.hashCode(), inputIndex);
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
		
		return slot == other.slot && inputIndex == other.inputIndex;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %s, %s)", slot, inputIndex, gssNode, sppfNode);
	}
	
}