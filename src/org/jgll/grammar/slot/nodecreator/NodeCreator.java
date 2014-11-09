package org.jgll.grammar.slot.nodecreator;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.generator.ConstructorCode;

public interface NodeCreator extends ConstructorCode{
	
	public SPPFNode create(GLLParser parser, BodyGrammarSlot slot, SPPFNode leftChild, SPPFNode rightChild);
	
}
