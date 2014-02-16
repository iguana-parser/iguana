package org.jgll.parser.gss;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.util.Input;


public class ArrayBasedGSSNodeFactory implements GSSNodeFactory {

	private final int inputSize;

	public ArrayBasedGSSNodeFactory(Input input) {
		this.inputSize = input.length();
	}
	
	@Override
	public GSSNode createGSSNode(GrammarSlot slot, int inputIndex) {
		return new ArrayBasedGSSNode((HeadGrammarSlot) slot, inputIndex, inputSize);
	}

}
