package org.jgll.grammar.condition;

import org.jgll.parser.GLLParser;

@FunctionalInterface
public interface SlotAction {
	
	default boolean execute(GLLParser parser) {
		return execute(parser, parser.getCurrentInputIndex());
	}
	
	public boolean execute(GLLParser parser, int inputIndex);
	
}
