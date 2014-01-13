package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {
	
	private static final int UNMATCHED = -1;
	private static final int ERROR = -2;

	private BitSet[] tokenIDs;
	
	/**
	 * tokens[inputIndex][tokenID] = length
 	 */
	private int[][] tokens;

	private Input input;

	private Grammar grammar;

	public GLLLexerImpl(Input input, Grammar grammar) {
		this.input = input;
		this.grammar = grammar;
		
		this.tokenIDs = new BitSet[input.length()];
		this.tokens = new int[input.length()][grammar.getCountTokens()];
		
		for(int i = 0; i < tokens.length; i++) {
			for(int j = 0; j < tokens[i].length; j++) {
				tokens[i][j] = UNMATCHED;
			}
		}
		
		for(int i = 0; i < tokenIDs.length; i++) {
			tokenIDs[i] = new BitSet();
		}
		
	}
	
	@Override
	public boolean match(int inputIndex, BitSet expectedTokens) {
		return tokenIDs[inputIndex].intersects(expectedTokens);
	}
	
	@Override
	public int tokenLengthAt(int inputIndex, int tokenID) {
		return tokens[inputIndex][tokenID];
	}
	
	@Override
	public List<Integer> tokensAt(int inputIndex, BitSet expectedTokens) {
		
		List<Integer> list = new ArrayList<>();
		
		for (int tokenID = expectedTokens.nextSetBit(0); tokenID >= 0; tokenID = expectedTokens.nextSetBit(tokenID+1)) {
			if(tokens[inputIndex][tokenID] == ERROR) {
				continue;
			}
			else if(tokens[inputIndex][tokenID] == UNMATCHED) {
				int length = grammar.getMatcher(tokenID).match(input, inputIndex);
				if(length >= 0) {
					list.add(tokenID);
				}
				tokens[inputIndex][tokenID] = length;
			} else {
				list.add(tokenID);
			}
		}
		
		return list;
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
