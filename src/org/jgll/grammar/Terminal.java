package org.jgll.grammar;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class Terminal implements Symbol {
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	/**
	 * Checks whether this terminal matches the given character from input
	 * 
	 * @param i the given input
	 *  
	 */
	public abstract boolean match(int i);
	
	/**
	 * Generates the code for matching against the current input 
	 */
	public abstract String getMatchCode();
	
	public int getId() {
		return id;
	}
}
