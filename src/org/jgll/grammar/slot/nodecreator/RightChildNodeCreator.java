package org.jgll.grammar.slot.nodecreator;

import java.io.Serializable;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.SPPFNode;

public class RightChildNodeCreator implements NodeCreator, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		return rightChild;
	}

}
