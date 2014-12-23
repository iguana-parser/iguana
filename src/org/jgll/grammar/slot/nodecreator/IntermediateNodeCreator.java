package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonPackedNode;

public class IntermediateNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		
		int leftExtent = leftChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		IntermediateNode newNode = sppfLookup.getIntermediateNode(slot, leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new IntermediateNodeCreator()";
	}

}
