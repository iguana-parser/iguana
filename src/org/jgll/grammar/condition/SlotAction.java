package org.jgll.grammar.condition;

import org.jgll.parser.GLLParser;

@FunctionalInterface
public interface SlotAction {
	
	public boolean execute(GLLParser parser);
	
}
