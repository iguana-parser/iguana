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
	
	public AbstractSymbol(SymbolBuilder<? extends Symbol> builder) {
		this.name = builder.name;
		this.label = builder.label;
		this.object = builder.object;
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
	public String toString() {
		String s = label == null ? name : label + ":" + name;
		if (!preConditions.isEmpty())
			s += listToString(preConditions) + " " + name;
		if (!postConditions.isEmpty())
			s += listToString(postConditions);
		return s;
	}
	
	@Override
	public String getConstructorCode() {
		return (label == null ? "" : ".setLabel(\"" + label + "\")") + 
			   (preConditions.isEmpty() ? "" : ".addPreConditions(" + asSet(preConditions) + ")") +
			   (postConditions.isEmpty() ? "" : ".addPostConditions(" + asSet(postConditions) + ")");
	}
	
}
