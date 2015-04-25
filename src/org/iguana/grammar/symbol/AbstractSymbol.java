package org.iguana.grammar.symbol;

import java.util.Set;

import org.iguana.datadependent.attrs.AbstractAttrs;
import org.iguana.grammar.condition.Condition;

import com.google.common.collect.ImmutableSet;

import static org.iguana.util.generator.GeneratorUtil.*;

public abstract class AbstractSymbol extends AbstractAttrs implements Symbol {

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
		this.preConditions = builder.preConditions; // TODO: Dangerous move: ImmutableSet.copyOf(builder.preConditions);
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
	public int size() {
		return 1;
	}

	
	@Override
	public String toString() {
		String s = label == null ? name : label + ":" + name;
		if (!preConditions.isEmpty())
			s += " " + listToString(preConditions);
		if (!postConditions.isEmpty())
			s += " " + listToString(postConditions);
		return s;
	}
	
	
	@Override
	public String toString(int j) {
		return this.toString() + (j == 1? " . " : "");
	}
	
	@Override
	public String getConstructorCode() {
		return (label == null ? "" : ".setLabel(\"" + label + "\")") + 
			   (preConditions.isEmpty() ? "" : ".addPreConditions(" + asSet(preConditions) + ")") +
			   (postConditions.isEmpty() ? "" : ".addPostConditions(" + asSet(postConditions) + ")");
	}
	
}
