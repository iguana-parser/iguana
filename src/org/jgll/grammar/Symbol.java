package org.jgll.grammar;

import java.io.Serializable;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface Symbol extends Serializable {
	
	public boolean isTerminal();
	
	public boolean isNonterminal();
	
	public String getName();
	
}
