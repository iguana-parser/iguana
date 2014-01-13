package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.EOF;
import org.jgll.regex.Matcher;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {

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
				tokens[i][j] = -1;
			}
		}
		
		for(int i = 0; i < tokenIDs.length; i++) {
			tokenIDs[i] = new BitSet();
		}
		
		tokenize(input);
	}

	@Override
	public BitSet tokenIDsAt(int index) {
		return tokenIDs[index];
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
		 for (int i = expectedTokens.nextSetBit(0); i >= 0; i = expectedTokens.nextSetBit(i+1)) {
			 if(tokens[inputIndex][i] >= 0) {
				 list.add(i);
			 }
		 }

		return list;
	}
	
	private void tokenize(Input input) {
		
		// Skip EOF 
		for (int i = 0; i < input.length() - 1; i++) {
			
			Set<Matcher> set = grammar.getTokensForChar(input.charAt(i));
			
			if (set == null) {
				continue;
			}
			
			for (Matcher dfa : set) {
				tokenize(i, input, dfa);
			}
		}
		
		tokens[input.length() - 1][EOF.TOKEN_ID] = 0;
		tokenIDs[input.length() - 1].set(EOF.TOKEN_ID);
	}

	
	private int tokenize(int inputIndex, Input input, Matcher dfa) {
		int length = dfa.match(input, inputIndex);
		if (length != -1) {
			createToken(inputIndex, dfa, length);
		}
		return length;
	}
	

	private void createToken(int inputIndex, Matcher dfa, int length) {
		tokenIDs[inputIndex].set(dfa.getId());
		tokens[inputIndex][dfa.getId()] = length;
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
