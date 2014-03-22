package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;

public class SecondAndLastTokenSlot extends LastTokenSlot {

	public SecondAndLastTokenSlot(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression,
			int tokenID, ConditionTest preConditions,
			ConditionTest postConditions) {
		super(id, nodeId, label, previous, regularExpression, tokenID, preConditions, postConditions);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public SPPFNode createNodeFromPop(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		return rightChild;
	}
}
