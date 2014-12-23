package org.jgll.grammar.slot;

import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public interface Transition {
	
	public void execute(GLLParser parser, Input input, NonPackedNode node);
	
	public GrammarSlot destination();
	
	public GrammarSlot origin();
	
}
