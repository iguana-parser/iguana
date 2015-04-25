package org.iguana.parser.descriptor;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;

/**
 * @author Ali Afroozeh
 * 
 */
// The label of SPPFNode is the same as the slot
public class Descriptor {
	
	// L
	private final BodyGrammarSlot slot;
	
	// (L1, i)
	private final GSSNode gssNode;
	
	// j
	private final int inputIndex;
	
	// (L, i, j)
	private final NonPackedNode sppfNode;
	
	public Descriptor(BodyGrammarSlot slot, GSSNode gssNode, int inputIndex, NonPackedNode sppfNode) {
		assert slot != null;
		assert gssNode != null;
		assert inputIndex >= 0;
		assert sppfNode != null;
		
		this.slot = slot;
		this.gssNode = gssNode;
		this.inputIndex = inputIndex;
		this.sppfNode = sppfNode;
	}
	
	public BodyGrammarSlot getGrammarSlot() {
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
	
	public void execute(GLLParser parser) {
		slot.execute(parser, gssNode, inputIndex, sppfNode);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %s, %s)", slot, inputIndex, gssNode, sppfNode);
	}
	
}