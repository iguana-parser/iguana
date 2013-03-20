package org.jgll.recognizer;

import org.jgll.grammar.HeadGrammarSlot;

public class GrammarInterpreterRecognizer extends AbstractGLLRecognizer {

	@Override
	protected void init() {
		
	}

	@Override
	protected void recognize(HeadGrammarSlot startSymbol) {
		startSymbol.recognize(this, input, u0, 0);
	}

}
