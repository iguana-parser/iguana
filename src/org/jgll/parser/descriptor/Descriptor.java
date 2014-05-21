package org.jgll.parser.descriptor;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;

/**
 * @author Ali Afroozeh
 * 
 */

public interface Descriptor {
	
	public GrammarSlot getGrammarSlot();
	
	public GSSNode getGSSNode();
	
	public int getInputIndex();
	
	public SPPFNode getSPPFNode();
	
}