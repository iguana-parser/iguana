package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.EOF;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.grammar.symbol.Token;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {

	private BitSet[] tokenIDs;
	
	/**
	 * tokens[inputIndex][tokenID] = length
 	 */
	private int[][] tokens;

	private Input input;

	private Grammar grammar;
	
	/**
	 * 
	 * A map from the current input value to the set of tokens that 
	 * start with the input value.
	 * 
	 */
	private Map<Integer, Set<Token>> tokensMap;
	
	public GLLLexerImpl(Input input, Grammar grammar) {
		this.input = input;
		this.grammar = grammar;
		
		this.tokenIDs = new BitSet[input.size()];
		this.tokens = new int[input.size()][grammar.getCountTokens()];
		this.tokensMap = new HashMap<>();
				
		for(int i = 0; i < tokens.length; i++) {
			for(int j = 0; j < tokens[i].length; j++) {
				tokens[i][j] = -1;
			}
		}
		
		for(int i = 0; i < tokenIDs.length; i++) {
			tokenIDs[i] = new BitSet();
		}
		
		for(Token token : grammar.getTokens()) {
			BitSet bitSet = token.asBitSet();
			 for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i+1)) {
				Set<Token> set = tokensMap.get(i);
				if(set == null) {
					set = new HashSet<>();
					tokensMap.put(i, set);
				}
				set.add(token);
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
	
	private void tokenize(String input) {
		for(int i = 0; i < input.length(); i++) {
			Set<Token> set = tokensMap.get((int)input.charAt(i));
			
			if(set == null) {
				continue;
			}
			
			// TODO: add a createToken method on the Token interface
			for(Symbol symbol : set) {
				if(symbol instanceof Keyword) {
					tokenize(i, input, (Keyword) symbol);
				} 
				else if (symbol instanceof RegularExpression) {
					tokenize(i, input, (RegularExpression) symbol);
				}
				else if(symbol instanceof Terminal) {
					tokenize(i, input, (Terminal) symbol);
				}
			}
		}
		
		tokens[input.length() - 1][EOF.TOKEN_ID] = 0;
		tokenIDs[input.length() - 1].set(EOF.TOKEN_ID);
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
	
	private void tokenize(int inputIndex, String input, Terminal terminal) {
		if(terminal.match(input.charAt(inputIndex))) {
			createToken(inputIndex, terminal, 1);
		}
	}

	private void createToken(int inputIndex, Symbol symbol, int length) {
		Integer tokenID = grammar.getTokenID(symbol);
		tokenIDs[inputIndex].set(tokenID);
		tokens[inputIndex][tokenID] = length;
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
