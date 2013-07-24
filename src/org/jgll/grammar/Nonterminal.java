package org.jgll.grammar;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;

public class Nonterminal implements Symbol {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final boolean ebnfList;
	
	private Condition pre;
	
	private Condition post;
	
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
	
	public Nonterminal pre(Condition pre) {
		Nonterminal newNonterminal = new Nonterminal(name, ebnfList);
		newNonterminal.pre = pre;
		return newNonterminal;
	}
	
	public Nonterminal post(Condition post) {
		Nonterminal newNonterminal = new Nonterminal(name, ebnfList);
		newNonterminal.post = post;
		return newNonterminal;
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

	public Condition getPreCondition() {
		return pre;
	}
	
	public Condition getPostCondition() {
		return post;
	}
}
