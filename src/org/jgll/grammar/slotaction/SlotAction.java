package org.jgll.grammar.slotaction;

import java.io.Serializable;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.GLLParserInternals;
import org.jgll.util.Input;

public interface SlotAction<T> extends Serializable {
	
	public T execute(GLLParserInternals parser, Input input);
	
	/**
	 * The condition from which this grammar slot is created. 
	 */
	public Condition getCondition();
	
}
