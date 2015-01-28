package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.util.generator.ConstructorCode;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public interface Symbol extends ConstructorCode, Serializable {
	
	public String getName();
	
	public Set<Condition> getPreConditions();
	
	public Set<Condition> getPostConditions();
	
	public Object getObject();
	
	public String getLabel();
	
	public SymbolBuilder<? extends Symbol> copyBuilder();
	
}	
