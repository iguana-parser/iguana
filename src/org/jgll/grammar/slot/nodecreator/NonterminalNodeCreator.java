package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.SPPFNode;

public class NonterminalNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		int leftExtent = leftChild.getLeftExtent();
		
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot lastGrammarSlot = (LastGrammarSlot) slot;
		
		NonterminalNode newNode = sppfLookup.getNonterminalNode(lastGrammarSlot.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, lastGrammarSlot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}

}
