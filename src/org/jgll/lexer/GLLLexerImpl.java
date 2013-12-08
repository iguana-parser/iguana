package org.jgll.lexer;

import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.util.Input;

public class GLLLexerImpl implements GLLLexer {

	private BitSet[] tokenIDs;
	
	private Set<Token>[] tokens;
	
	private Map<Symbol, Integer> idMap;
	
	public GLLLexerImpl(Input input, Grammar grammar) {
		tokenIDs = new BitSet[input.size()];
		tokenize(input.toString(), grammar);
		
		int i = 0;
		idMap = new HashMap<Symbol, Integer>();
		for(RegularExpression regex : grammar.getRegularExpressions()) {
			idMap.put(regex, i++);
		}
		for(Keyword keyword : grammar.getKeywords()) {
			idMap.put(keyword, i++);
		}
	}
	
	@Override
	public BitSet tokenIDsAt(int index) {
		return tokenIDs[index];
	}
	
	@Override
	public Set<Token> tokensAt(int index) {
		return tokens[index];
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
		tokenIDs[inputIndex].set(idMap.get(symbol));
		Set<Token> set = tokens[inputIndex];
		if(set == null) {
			set = new HashSet<>();
		}
		set.add(new Token(idMap.get(symbol), length));
	}
	
}
