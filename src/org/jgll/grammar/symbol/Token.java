package org.jgll.grammar.symbol;

import java.util.BitSet;

public interface Token {

	/** 
	 * Returns the first characters that can be matched by this regular expression.
	 */
	public BitSet asBitSet();
	
}
