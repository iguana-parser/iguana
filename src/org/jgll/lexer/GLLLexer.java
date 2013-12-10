package org.jgll.lexer;

import java.util.BitSet;

import org.jgll.util.Input;


public interface GLLLexer {
	
	/**
	 * Returns the list of available token indices at the 
	 * given input index, encoded as a BitSet. 
	 * 
	 * @param index the given input index
	 */
	public BitSet tokenIDsAt(int index);
	
	public Token tokenAt(int inputIndex, BitSet expectedTokens);
	
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
	public int tokenAt(int inputIndex, int tokenID);
	
	public Input getInput();

}
