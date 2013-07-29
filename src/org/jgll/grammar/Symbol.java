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
	
	public Iterable<Condition> getPreConditions();
	
	public Iterable<Condition> getPostConditions();
	
}
