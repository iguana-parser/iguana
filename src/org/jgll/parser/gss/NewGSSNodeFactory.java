package org.jgll.parser.gss;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;


public class NewGSSNodeFactory implements GSSNodeFactory {

	@Override
	public GSSNode createGSSNode(GrammarSlot slot, int inputIndex) {
		return new GSSNodeImpl((HeadGrammarSlot)slot, inputIndex);
	}

}
