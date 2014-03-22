package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.NonterminalGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.SPPFNode;

public class SecondNonterminalSlot extends NonterminalGrammarSlot {

	private static final long serialVersionUID = 1L;

	public SecondNonterminalSlot(int id, int nodeId, String label,
			BodyGrammarSlot previous, HeadGrammarSlot nonterminal,
			ConditionTest preConditions, ConditionTest postConditions) {
		super(id, nodeId, label, previous, nonterminal, preConditions, postConditions);
	}
	
	@Override
	public SPPFNode createNodeFromPop(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		return rightChild;
	}
}
