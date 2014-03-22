package org.jgll.grammar.slot.nodecreator;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

public class NonterminalNodeCreator implements NodeCreator {

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		int leftExtent = leftChild.getLeftExtent();
		
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot lastGrammarSlot = (LastGrammarSlot) slot;
		
		NonterminalSymbolNode newNode = sppfLookup.getNonterminalNode(lastGrammarSlot.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, lastGrammarSlot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}

}
