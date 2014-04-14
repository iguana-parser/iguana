package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.Collection;
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
	
	public Symbol addCondition(Condition condition);
	
	public Symbol addConditions(Collection<Condition> conditions);
	
	public Set<Condition> getConditions();
	
	public Symbol clone();
	
}	
