package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;

public class RightChildNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return rightChild;
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new RightChildNodeCreator()";
	}

}
