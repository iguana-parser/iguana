package org.jgll.grammar.slotnew;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


public interface Transition {
	
	public void execute(GLLParser parser, Input input, NonPackedNode node);
	
	public GrammarSlot destination();
	
	public GrammarSlot origin();
	
}
