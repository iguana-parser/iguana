package org.jgll.grammar.slot.nodecreator;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;

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
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
		return DummyNode.getInstance();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "DummyNodeCreator.getInstance()";
	}
	
}
