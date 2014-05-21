package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.ExternalHasher;

public class DescriptorImpl implements Descriptor {
	
	/**
	 * The label that indicates the parser code to execute for the encountered
	 * nonterminal.
	 */
	protected final GrammarSlot slot;
	
	/**
	 * The associated GSSNode.
	 */
	protected final GSSNode gssNode;
	
	/**
	 * The current index in the input string.
	 */
	protected final int inputIndex;
	
	/**
	 * The SPPF node that was created before parsing the encountered 
	 * nonterminal.
	 */
	protected final SPPFNode sppfNode;

	private ExternalHasher<Descriptor> e;
	
	public DescriptorImpl(ExternalHasher<Descriptor> e, GrammarSlot slot, GSSNode gssNode, int inputIndex, SPPFNode sppfNode) {
		this.e = e;
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
		return e.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) { 
			return true;
		}
		
		if(! (obj instanceof Descriptor)) {
			return false;
		}
		
		return e.equals(this, (Descriptor)obj);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %s, %s)", slot, inputIndex, gssNode, sppfNode);
	}

}
