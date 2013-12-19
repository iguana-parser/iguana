package org.jgll.lexer;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
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
	
		
	public GLLLexerImpl(Input input, Grammar grammar) {
		this.input = input;
		this.grammar = grammar;
		
		this.tokenIDs = new BitSet[input.size()];
		this.tokens = new int[input.size()][grammar.getCountTokens()];
		
		for(int i = 0; i < tokens.length; i++) {
			for(int j = 0; j < tokens[i].length; j++) {
				tokens[i][j] = -1;
			}
		}
		
		for(int i = 0; i < tokenIDs.length; i++) {
			tokenIDs[i] = new BitSet();
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
			
			Set<Token> set = grammar.getTokensForChar(input.charAt(i));
			
			if(set == null) {
				continue;
			}
			
			for(Token token : set) {
				
				if(token instanceof Keyword) {
					tokenize(i, input, (Keyword) token);
				} 
				else if (token instanceof RegularExpression) {
					tokenize(i, input, (RegularExpression) token);
				}
				else if(token instanceof Terminal) {
					tokenize(i, input, (Terminal) token);
				}
			}
		}
		
		tokens[input.length() - 1][EOF.TOKEN_ID] = 0;
		tokenIDs[input.length() - 1].set(EOF.TOKEN_ID);
	}
	
	private int tokenize(int inputIndex, String input, Keyword keyword) {
		if(keyword.match(input, inputIndex)) {
			createToken(inputIndex, keyword, keyword.size());
			return keyword.size();
		}
		return -1;
	}
	
	private int tokenize(int inputIndex, String input, RegularExpression regex) {
		int length = regex.getAutomaton().run(input, inputIndex);
		if(length != -1) {
			createToken(inputIndex, regex, length);
		}
		return length;
	}
	
	private int tokenize(int inputIndex, String input, Terminal terminal) {
		if(terminal.match(input.charAt(inputIndex))) {
			createToken(inputIndex, terminal, 1);
			return 1;
		}
		return -1;
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
