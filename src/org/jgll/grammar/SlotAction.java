package org.jgll.grammar;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;

public interface SlotAction {
	
	public void execute(GrammarSlot slot, GLLParser parser, Input input, int ci, GSSNode cu, SPPFNode cn);
	
}
