package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;

public class NonterminalNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		int leftExtent = leftChild.getLeftExtent();
		
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot lastGrammarSlot = (LastGrammarSlot) slot;
		
		NonterminalNode newNode = sppfLookup.getNonterminalNode(lastGrammarSlot.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, lastGrammarSlot, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new NonterminalNodeCreator()";
	}

}
