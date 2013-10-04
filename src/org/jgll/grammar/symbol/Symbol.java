package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.Collection;

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
	
	
	/**
	 * Creates a copy of the current symbol and adds the given conditions
	 * to it. 
	 */
	public Symbol addConditions(Collection<Condition> conditions);
	
	public Collection<Condition> getConditions();
	
}
