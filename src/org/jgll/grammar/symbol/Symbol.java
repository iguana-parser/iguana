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
	
	public Object getObject();
	
	public String getLabel();
	
	public SymbolBuilder<? extends Symbol> builder();
	
}	
