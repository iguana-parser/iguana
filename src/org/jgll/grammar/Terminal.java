package org.jgll.grammar;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class Terminal implements Symbol{
	
	private static final long serialVersionUID = 1L;
	
	protected int id;
	
	public abstract boolean match(int i);
	
	public abstract String getMatchCode();
	
	public int getId() {
		return id;
	}
}
