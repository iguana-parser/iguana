package org.jgll.grammar.slot.nodecreator;

import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.generator.ConstructorCode;

public interface NodeCreator extends ConstructorCode {
	
	public NonPackedNode create(GLLParser parser, BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild);
	
}
