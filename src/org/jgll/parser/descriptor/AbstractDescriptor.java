package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;

public abstract class AbstractDescriptor implements Descriptor {
	
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
	
	public AbstractDescriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode) {
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

	public SPPFNode getSPPFNode() {
		return sppfNode;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(slot.getId(), 
				    							   sppfNode.getId(), 
				    							   inputIndex);
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
		
		return slot == other.getGrammarSlot() &&
			   sppfNode.getId() == other.getSPPFNode().getId() &&
			   inputIndex == other.getInputIndex();
	}
	
	@Override
	public String toString() {
		return "(" + slot + ", " + inputIndex + ", " +
			   "(" + gssNode.getGrammarSlot() + ", " + gssNode.getInputIndex() + ")" +
			   ", " + sppfNode + ")";
	}

}
