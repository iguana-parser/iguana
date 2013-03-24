package org.jgll.recognizer;

import org.jgll.grammar.HeadGrammarSlot;

public class GrammarInterpreterRecognizer extends AbstractGLLRecognizer {

	@Override
	protected void init() {
		
	}

	@Override
	protected void recognize(HeadGrammarSlot startSymbol) {
		startSymbol.recognize(this, input);
	}

	@Override
	public int getCi() {
		return ci;
	}

	@Override
	public GSSNode getCu() {
		return cu;
	}

}
