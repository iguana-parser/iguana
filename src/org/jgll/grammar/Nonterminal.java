package org.jgll.grammar;

import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;

public class Nonterminal implements Symbol {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final boolean ebnfList;
	
	private List<Condition> preConditions;
	
	private List<Condition> postConditions;
	
	public Nonterminal(String name) {
		this(name, false);
	}

	public Nonterminal(String name, boolean ebnfList) {
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
	
	public Nonterminal AddPreCondition(Condition condition) {
		preConditions.add(condition);
		return this;
	}
	
	public Nonterminal addPostCondition(Condition condition) {
		postConditions.add(condition);
		return this;
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
		return HashFunctions.defaulFunction().hash(name.hashCode());
	}

	@Override
	public Iterable<Condition> getPreConditions() {
		return preConditions;
	}

	@Override
	public Iterable<Condition> getPostConditions() {
		return postConditions;
	}

}
