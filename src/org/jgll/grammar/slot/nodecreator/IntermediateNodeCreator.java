package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.SPPFNode;

public class IntermediateNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		
		int leftExtent = leftChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		IntermediateNode newNode = sppfLookup.getIntermediateNode(slot, leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, slot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}

	@Override
	public String getConstructorCode() {
		return "new IntermediateNodeCreator()";
	}

}
