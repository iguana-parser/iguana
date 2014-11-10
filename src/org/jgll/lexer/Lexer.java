package org.jgll.lexer;

import java.util.BitSet;

import org.jgll.util.Input;


public interface Lexer {

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
	 * 
	 * @param inputIndex
	 * @param tokenID
	 */
	public void setTokenAt(int inputIndex, int tokenID, int length);
	
	/**
	 * Returns the underlying input object. 
	 */
	public Input getInput();

}
