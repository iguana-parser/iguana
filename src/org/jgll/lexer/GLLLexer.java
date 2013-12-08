package org.jgll.lexer;

import java.util.BitSet;
import java.util.Set;


public interface GLLLexer {
	
	/**
	 * Returns the list of available token indices at the 
	 * given input index, encoded as a BitSet. 
	 * 
	 * @param index the given input index
	 */
	public BitSet tokenIDsAt(int index);
	
	/**
	 * 
	 * 
	 * @param index
	 * @return
	 */
	public Set<Token> tokensAt(int index);

}
