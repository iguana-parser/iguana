package org.jgll.lexer;

import java.util.BitSet;


public interface GLLLexer {
	
	/**
	 * Returns the list of available token indices at the 
	 * given input index, encoded as a BitSet. 
	 * 
	 * @param index the given input index
	 */
	public BitSet tokensAt(int index);

}
