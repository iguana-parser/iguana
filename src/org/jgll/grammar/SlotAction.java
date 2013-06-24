package org.jgll.grammar;

import java.io.Serializable;

import org.jgll.parser.GLLParser;
import org.jgll.util.Input;

public interface SlotAction<T> extends Serializable {
	
	public T execute(GLLParser parser, Input input);
	
}
