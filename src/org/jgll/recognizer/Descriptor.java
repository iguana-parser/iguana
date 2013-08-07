package org.jgll.recognizer;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.HashFunction;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class Descriptor {
	
	public static final ExternalHasher<Descriptor> externalHasher = new DescriptorExternalHasher();
	
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
	
	
	public Descriptor(GrammarSlot slot, GSSNode gssNode, int inputIndex) {
		
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
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
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
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

		return inputIndex == other.getInputIndex() &&
			   slot == other.slot &&
			   gssNode.getGrammarSlot().getId() == other.gssNode.getGrammarSlot().getId() &&
			   gssNode.getInputIndex() == other.gssNode.getInputIndex();
	}
	
	@Override
	public String toString() {
		return "(" + slot + ", " + inputIndex + ", " + gssNode + ")";
	}

	public static class DescriptorExternalHasher implements ExternalHasher<Descriptor> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(Descriptor descriptor, HashFunction f) {
			return f.hash(descriptor.slot.getId(),
						  descriptor.inputIndex,
						  descriptor.gssNode.getGrammarSlot().getId(),
						  descriptor.gssNode.getInputIndex());
			}
	}
	
}
