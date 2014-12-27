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
public interface Symbol extends Serializable {
	
	public String getName();
	
	public Set<Condition> getPreConditions();
	
	public Set<Condition> getPostConditions();
	
	public Object getObject();
	
	public String getLabel();
	
}	
