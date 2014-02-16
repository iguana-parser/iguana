package org.jgll.parser.gss;

import org.jgll.grammar.slot.GrammarSlot;


public interface GSSNodeFactory {
	
	public GSSNode createGSSNode(GrammarSlot slot, int inputIndex);

}
