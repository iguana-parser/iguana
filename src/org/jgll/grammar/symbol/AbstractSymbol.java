package org.jgll.grammar.symbol;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.Set;

import org.jgll.grammar.condition.Condition;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractSymbol implements Symbol {

	private static final long serialVersionUID = 1L;
	
	protected final Set<Condition> preConditions;
	
	protected final Set<Condition> postConditions;
	
	protected final String name;
	
	protected final Object object;
	
	protected final String label;
	
	protected final String variable;
	
	public AbstractSymbol(SymbolBuilder<? extends Symbol> builder) {
		this.name = builder.name;
		this.label = builder.label;
		this.object = builder.object;
		this.variable = builder.variable;
		this.preConditions = ImmutableSet.copyOf(builder.preConditions);
		this.postConditions = ImmutableSet.copyOf(builder.postConditions);
	}
	
	@Override
	public Set<Condition> getPreConditions() {
		return preConditions;
	}
	
	@Override
	public Set<Condition> getPostConditions() {
		return postConditions;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Object getObject() {
		return object;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	@Override
	public String getVariable() {
		return variable;
	}
	
	@Override
	public String toString() {
		return preConditions.isEmpty() ? name :  "(" + name + listToString(preConditions) + ")";
	}
	
}
