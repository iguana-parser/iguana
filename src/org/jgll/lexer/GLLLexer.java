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
	
	public Input getInput();

}
