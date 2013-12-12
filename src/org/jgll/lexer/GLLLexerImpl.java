package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Symbol;
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
		this.tokenIDs = new BitSet[input.size()];
		tokens = new int[input.size()][grammar.getCountTokens()];
		
		for(int k = 0; k < tokens.length; k++) {
			for(int j = 0; j < tokens[k].length; j++) {
				tokens[k][j] = -1;
			}
		}
		
		tokenize(input.toString());
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
	public int tokenAt(int inputIndex, int tokenID) {
		return tokens[inputIndex][tokenID];
	}
	
	@Override
	public List<Integer> tokensAt(int inputIndex, BitSet expectedTokens) {
		List<Integer> list = new ArrayList<>();
		 for (int i = expectedTokens.nextSetBit(0); i >= 0; i = expectedTokens.nextSetBit(i+1)) {
			 if(tokens[inputIndex][i] > 0) {
				 list.add(i);
			 }
		 }

		return list;
	}
	
	private void tokenize(String input) {
		for(int i = 0; i < input.length(); i++) {
			for(Symbol symbol : grammar.getTokens()) {
				if(symbol instanceof Keyword) {
					tokenize(i, input, (Keyword) symbol);
				} else if (symbol instanceof RegularExpression) {
					tokenize(i, input, (RegularExpression) symbol);
				}
			}
		}
	}
	
	private void tokenize(int inputIndex, String input, Keyword keyword) {
		if(keyword.match(input, inputIndex)) {
			createToken(inputIndex, keyword, keyword.size());
		}
	}
	
	private void tokenize(int inputIndex, String input, RegularExpression regex) {
		int length = regex.getAutomaton().run(input, inputIndex);
		if(length != -1) {
			createToken(inputIndex, regex, length);
		}
	}

	private void createToken(int inputIndex, Symbol symbol, int length) {
		Integer tokenID = grammar.getTokenID(symbol);
		
		if(tokenIDs[inputIndex] == null) {
			tokenIDs[inputIndex]= new BitSet();
		}
		
		tokenIDs[inputIndex].set(tokenID);
		tokens[inputIndex][tokenID] = length;
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
