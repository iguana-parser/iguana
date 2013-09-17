package org.jgll.recognizer;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.recognizer.lookup.Lookup;

public class InterpretedGLLRecognizer extends AbstractGLLRecognizer {

	public InterpretedGLLRecognizer(Lookup lookup) {
		super(lookup);
	}
	
	@Override
	public void add(GrammarSlot slot, GSSNode u, int inputIndex) {
		if(slot == startSlot && inputIndex == endIndex && u == u0) {
			recognized = true;
			return;
		}

		super.add(slot, u, inputIndex);
	}
	
}
