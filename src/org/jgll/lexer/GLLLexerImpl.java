package org.jgll.lexer;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

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
	private Token[][] tokens;
	
	private Map<Symbol, Integer> idMap;

	private Input input;
	
	public GLLLexerImpl(Input input, Grammar grammar) {
		this.input = input;
		this.tokenIDs = new BitSet[input.size()];
		int i = 0;
		idMap = new HashMap<Symbol, Integer>();
		for(RegularExpression regex : grammar.getRegularExpressions()) {
			idMap.put(regex, i++);
		}
		for(Keyword keyword : grammar.getKeywords()) {
			idMap.put(keyword, i++);
		}
		
		tokens = new Token[input.size()][idMap.size()];
		tokenize(input.toString(), grammar);
	}
	
	@Override
	public BitSet tokenIDsAt(int index) {
		return tokenIDs[index];
	}
	
	@Override
	public Token tokenAt(int inputIndex, BitSet expectedTokens) {
		return null;
	}
	
	@Override
	public int tokenAt(int inputIndex, int tokenID) {
		Token token = tokens[inputIndex][tokenID];
		if(token == null) {
			return -1;
		} else {
			return token.getLength();			
		}
	}
	
	private void tokenize(String input, Grammar grammar) {
		for(int i = 0; i < input.length(); i++) {
			for(RegularExpression regex : grammar.getRegularExpressions()) {
				tokenize(i, input, regex);
			}
			for(Keyword keyword : grammar.getKeywords()) {
				tokenize(i, input, keyword);
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
		Integer tokenID = idMap.get(symbol);
		
		if(tokenIDs[inputIndex] == null) {
			tokenIDs[inputIndex]= new BitSet();
		}
		
		tokenIDs[inputIndex].set(tokenID);
		tokens[inputIndex][tokenID] = new Token(tokenID, length);
	}
	
	@Override
	public Input getInput() {
		return input;
	}

}
