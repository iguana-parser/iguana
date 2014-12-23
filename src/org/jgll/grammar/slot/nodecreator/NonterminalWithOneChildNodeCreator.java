package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;

public class NonterminalWithOneChildNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		int leftExtent = rightChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot last = (LastGrammarSlot) slot;
		NonterminalNode newNode = sppfLookup.getNonterminalNode(last.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, last, rightChild.getLeftExtent(), DummyNode.getInstance(), rightChild);
		return newNode;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new NonterminalWithOneChildNodeCreator()";
	}
	
}
