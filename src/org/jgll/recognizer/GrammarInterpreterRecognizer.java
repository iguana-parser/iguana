package org.jgll.recognizer;

import java.util.ArrayDeque;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.util.hashing.CuckooHashSet;

public class GrammarInterpreterRecognizer extends AbstractGLLRecognizer {

	@Override
	protected void recognize(HeadGrammarSlot startSymbol) {
		L0.getInstance().recognize(this, input, startSymbol);
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
