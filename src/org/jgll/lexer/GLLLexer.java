package org.jgll.lexer;

import java.util.BitSet;
import java.util.List;

import org.jgll.util.Input;


public interface GLLLexer {

	public List<Integer> tokensAt(int inputIndex, BitSet expectedTokens);
	
	/**
	 * @param inputIndex
	 * @param expectedTokens
	 * 
	 * @return true if at least one of the expected tokens matches at the given input index.
	 */
	public boolean match(int inputIndex, BitSet expectedTokens);
	
	/**
	 * 
	 * Returns the length of the token found at the given input index
	 * for the given token id. 
	 * 
	 * @param inputIndex
	 * @param tokenID
	 * 
	 * @return -1 if no such tokens exists at the given input index.
	 */
	public int tokenLengthAt(int inputIndex, int tokenID);
	
	/**
	 * Returns the underlying input object. 
	 */
	public Input getInput();

}
