package org.jgll.grammar;

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
	
	public Symbol addConditions(Collection<Condition> conditions);
	
	public Collection<Condition> getConditions();
	
}
