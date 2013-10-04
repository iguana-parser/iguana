package org.jgll.grammar.symbol;

import java.util.Collection;

import org.jgll.grammar.condition.Condition;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final boolean ebnfList;
	
	private boolean collapsible;
	
	public Nonterminal(String name) {
		this(name, false);
	}

	public Nonterminal(String name, boolean ebnfList) {
		this.name = name;
		this.ebnfList = ebnfList;
	}
	
	public Nonterminal(String name, boolean ebnfList, Iterable<Condition> conditions) {
		super(conditions);
		this.name = name;
		this.ebnfList = ebnfList;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isEbnfList() {
		if(ebnfList == true){
			return true;
		} else{
			if(name.startsWith("List")) {
				return true;
			}
		} 

		return false;
	}
	
	
	public void setCollapsible(boolean collapsible) {
		this.collapsible = collapsible;
	}
	
	public boolean isCollapsible() {
		return collapsible;
	}
	
	@Override
	public Nonterminal addConditions(Collection<Condition> conditions) {
		Nonterminal nonterminal = new Nonterminal(this.name);
		nonterminal.conditions.addAll(this.conditions);
		nonterminal.conditions.addAll(conditions);
		return nonterminal;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Nonterminal)) {
			return false;
		}
		
		Nonterminal other = (Nonterminal) obj;
		
		return name.equals(other.name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
