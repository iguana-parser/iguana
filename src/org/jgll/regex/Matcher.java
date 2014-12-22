package org.jgll.regex;

import org.jgll.util.Input;


@FunctionalInterface
public interface Matcher {
	
	public int match(Input input, int i);
	
}
