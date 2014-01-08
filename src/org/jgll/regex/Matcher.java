package org.jgll.regex;

import org.jgll.util.Input;

public interface Matcher {
	
	public boolean match(Input input);
	
	public int match(Input input, int inputIndex);
	
	/**
	 * Returns true if can match the given input from the provided start position (inclusive)
	 * to the provided end position (exclusive).
	 *  
	 */
	public boolean match(Input input, int start, int end);
	
	public int getId();
	
	public void setId(int id);

}
