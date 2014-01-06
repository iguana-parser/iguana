package org.jgll.regex;

import org.jgll.util.Input;


public interface Matcher {
	
	public boolean match(Input input);
	
	public int match(Input input, int inputIndex);
	
	public int getId();
	
	public void setId(int id);

}
