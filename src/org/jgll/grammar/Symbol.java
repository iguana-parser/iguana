package org.jgll.grammar;

import java.io.Serializable;

import org.jgll.grammar.condition.Condition;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface Symbol extends Serializable {
	
	public String getName();
	
	public Symbol addCondition(Condition condition);
	
	public Iterable<Condition> getConditions();
	
}
