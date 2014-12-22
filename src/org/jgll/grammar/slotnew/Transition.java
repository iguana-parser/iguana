package org.jgll.grammar.slotnew;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.GLLParser;
import org.jgll.util.Input;


public interface Transition {
	
	public GrammarSlot execute(GLLParser parser, Input input, int i);
	
	public GrammarSlot destination();
	
	public GrammarSlot origin();
	
}
