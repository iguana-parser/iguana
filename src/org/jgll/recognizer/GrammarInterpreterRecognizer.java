package org.jgll.recognizer;

import java.util.ArrayDeque;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.L0;
import org.jgll.util.Input;
import org.jgll.util.hashing.CuckooHashSet;

public class GrammarInterpreterRecognizer extends AbstractGLLRecognizer {

	@Override
	protected void init() {
		if(descriptorSet == null) {
			descriptorSet = new CuckooHashSet<>();
		} else {
			descriptorSet.clear();
		}
		
		if(descriptorStack == null) {
			descriptorStack = new ArrayDeque<>();
		} else {
			descriptorStack.clear();
		}
		
		if(gssNodes == null) {
			gssNodes = new CuckooHashSet<>();
		} else {
			gssNodes.clear();
		}
		
		cu = GSSNode.U0;
	}

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

	@Override
	public boolean recognizePrefix(Input input, BodyGrammarSlot slot) {
		// TODO Auto-generated method stub
		return false;
	}
}
