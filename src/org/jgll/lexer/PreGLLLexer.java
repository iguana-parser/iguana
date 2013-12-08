package org.jgll.lexer;

import java.util.BitSet;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.util.Input;

public class PreGLLLexer implements GLLLexer {

	private BitSet[] tokens;
	
	public PreGLLLexer(Input input, Grammar grammar) {
		tokens = new BitSet[input.size()];
		tokenize(input, grammar);
	}
	
	@Override
	public BitSet tokensAt(int index) {
		return null;
	}
	
	private void tokenize(Input input, Grammar grammar) {
		for(int i = 0; i < input.size(); i++) {
			for(RegularExpression regex : grammar.getRegularExpressions()) {
				tokenize(i, input, regex);
			}			
		}
	}
	
	private void tokenize(int inputIndex, Input input, RegularExpression regex) {
		if(regex.getAutomaton().run("")) {
			tokens[inputIndex].set(1);
		}
	}

}
