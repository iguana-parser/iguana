package org.jgll.lexer;

import java.util.BitSet;

import org.jgll.regex.Matcher;
import org.jgll.util.Input;


public interface GLLLexer {

	/**
	 * Matches the input from the given input index using the set of expected tokens.
	 * 
	 * @param inputIndex
	 * @param expectedTokens
	 * 
	 * @return true if at least one of the expected tokens matches the input at the given index.
	 */
	public boolean match(int inputIndex, Matcher m);
	
	public int tokenAt(int inputIndex, BitSet set);
	
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
