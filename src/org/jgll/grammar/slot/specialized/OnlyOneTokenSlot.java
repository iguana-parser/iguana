package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;

/**
 * Handles the case A ::= .x where x is a token grammar slot 
 * 
 * @author Ali Afroozeh
 *
 */
public class OnlyOneTokenSlot extends LastTokenSlot {

	private static final long serialVersionUID = 1L;

	public OnlyOneTokenSlot(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression,
			int tokenID, ConditionTest preConditions,
			ConditionTest postConditions) {
		
		super(id, nodeId, label, previous, regularExpression, tokenID, preConditions, postConditions);
	}
	
	@Override
	public SPPFNode createNode(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		
		assert leftChild == null;
		
		int leftExtent = rightChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot last = (LastGrammarSlot) next;
		NonterminalSymbolNode newNode = sppfLookup.getNonterminalNode(last.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, last, rightChild.getLeftExtent(), DummyNode.getInstance(), rightChild);
		
		return newNode;
	}

	@Override
	public SPPFNode createNodeFromPop(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		throw new UnsupportedOperationException("Cannot create a node from this slot at pop");
	}


}
