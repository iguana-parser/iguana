package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.EOF;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {
	
	private static final int UNMATCHED = -1;
	private static final int ERROR = -2;

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
	public boolean match(int inputIndex, int[] expectedTokens) {
		
		for(int i = 0; i < expectedTokens.length; i++) {
			int tokenID = expectedTokens[i];
			
			if(tokenID == EOF.TOKEN_ID) {
				if(input.charAt(inputIndex) == 0) {
					return true;
				}
				continue;
			}
			
			if(tokens[inputIndex][tokenID] == ERROR) {
				continue;
			}
			else if(tokens[inputIndex][tokenID] == UNMATCHED) {
				int length = grammar.getMatcher(tokenID).match(input, inputIndex);
				if(length >= 0) {
					tokens[inputIndex][tokenID] = length;
					return true;
				}
			} else {
				return true;
			}
		}
		
		return false;
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
	public List<Integer> tokensAt(int inputIndex, BitSet expectedTokens) {
		
		List<Integer> list = new ArrayList<>();
		
		for(int tokenID = expectedTokens.nextSetBit(0); tokenID >= 0; tokenID = expectedTokens.nextSetBit(tokenID+1)) {
			
			// EOF only matches at the end of the file
			if(tokenID == EOF.TOKEN_ID) {
				if(input.charAt(inputIndex) == 0) {
					list.add(EOF.TOKEN_ID);
				}
				continue;
			}
			
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
