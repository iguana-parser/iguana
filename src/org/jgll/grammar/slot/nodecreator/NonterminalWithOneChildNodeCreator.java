package org.jgll.grammar.slot.nodecreator;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

public class NonterminalWithOneChildNodeCreator implements NodeCreator {

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		int leftExtent = rightChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot last = (LastGrammarSlot) slot;
		NonterminalSymbolNode newNode = sppfLookup.getNonterminalNode(last.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, last, rightChild.getLeftExtent(), DummyNode.getInstance(), rightChild);
		return newNode;
	}
	
}
