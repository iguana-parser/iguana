package org.jgll.grammar.slot.nodecreator;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.SPPFNode;

public class DummyNodeCreator implements NodeCreator {
	
	private static DummyNodeCreator nodeCreator;
	
	public static DummyNodeCreator getInstance() {
		if (nodeCreator == null) {
			nodeCreator = new DummyNodeCreator();
		}
		return nodeCreator;
	}
	
	private DummyNodeCreator() {}

	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild) {
		return DummyNode.getInstance();
	}

}
