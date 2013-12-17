package org.jgll.grammar.symbol;

import java.util.BitSet;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface Terminal extends Symbol, Token {
	
	/**
	 * Checks whether this terminal matches the given character from input
	 * 
	 * @param i the given input
	 *  
	 */
	public boolean match(int i);
	
	/**
	 * Generates the code for matching against the current input 
	 */
	public String getMatchCode();
	
	@Override
	public BitSet asBitSet();
	
}
