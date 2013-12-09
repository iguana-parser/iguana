package org.jgll.grammar.slotaction;

import java.io.Serializable;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParserInternals;

public interface SlotAction<T> extends Serializable {
	
	public T execute(GLLParserInternals parser, GLLLexer lexer);
	
	/**
	 * The condition from which this grammar slot is created. 
	 */
	public Condition getCondition();
	
}
