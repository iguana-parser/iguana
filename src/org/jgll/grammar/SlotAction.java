package org.jgll.grammar;

import org.jgll.parser.GLLParser;
import org.jgll.util.Input;

public interface SlotAction<T> {
	
	public T execute(GLLParser parser, Input input);
	
}
