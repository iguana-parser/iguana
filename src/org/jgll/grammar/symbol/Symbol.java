package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.condition.Condition;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface Symbol extends Serializable, Cloneable {
	
	public String getName();
	
	public Set<Condition> getConditions();
	
	/**
	 * Creates a copy of this symbol with the given conditions
	 * 
	 * @param conditions the given conditions.
	 */
	public Symbol withConditions(Set<Condition> conditions);
	
	/**
	 * Creates a copy of this symbol with the given condition.
	 * 
	 * @param condition the given codintion.
	 */
	public Symbol withCondition(Condition condition);
	
}	
