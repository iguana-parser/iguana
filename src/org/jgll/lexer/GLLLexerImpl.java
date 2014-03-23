package org.jgll.lexer;

import java.util.BitSet;

import org.jgll.grammar.Grammar;
import org.jgll.regex.matcher.Matcher;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {
	
	private static final int UNMATCHED = -1;

	/**
	 * tokens[inputIndex][tokenID] = length
 	 */
	private int[][] tokens;

	private Input input;

	private Grammar grammar;

	public GLLLexerImpl(Input input, Grammar grammar) {
		this.input = input;
		this.grammar = grammar;
		
		this.tokens = new int[input.length()][grammar.getCountTokens()];
		
		for(int i = 0; i < tokens.length; i++) {
			for(int j = 0; j < tokens[i].length; j++) {
				tokens[i][j] = UNMATCHED;
			}
		}
	}
	
	@Override
	public boolean match(int inputIndex, Matcher m) {
		return m.match(input, inputIndex) > 0;
	}
	
	@Override
	public int tokenAt(int inputIndex, BitSet set) {
		for (int i = set.nextSetBit(0); i >= 0; i = set.nextSetBit(i+1)) {
			if(tokens[inputIndex][i] >= 0) {
				return i;
			} else {
				int length = grammar.getMatcher(i).match(input, inputIndex);
				tokens[inputIndex][i] = length;
				
				if(length >= 0) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public void setTokenAt(int inputIndex, int tokenID, int length) {
		System.out.println(String.format("%d, %d, %d", inputIndex, tokenID, length));
		tokens[inputIndex][tokenID] = length;
	}
	
	@Override
	public int tokenLengthAt(int inputIndex, int tokenID) {
		if(tokens[inputIndex][tokenID] == UNMATCHED) {
			int length = grammar.getMatcher(tokenID).match(input, inputIndex);
			tokens[inputIndex][tokenID] = length;
			return length;
		}
		return tokens[inputIndex][tokenID];
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
