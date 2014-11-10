package org.jgll.grammar.slotaction;

import org.jgll.grammar.condition.Condition;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.generator.ConstructorCode;

public interface SlotAction<T> extends ConstructorCode {
	
	public T execute(GLLParser parser, Lexer lexer, GSSNode gssNode, int inputIndex);
	
	/**
	 * The condition from which this grammar slot is created. 
	 */
	public Condition getCondition();
	
}
