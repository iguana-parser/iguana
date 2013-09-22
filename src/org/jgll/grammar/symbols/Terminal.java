package org.jgll.grammar.symbols;

import java.util.BitSet;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface Terminal extends Symbol {
	
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
	
	public BitSet asBitSet();
	
}
