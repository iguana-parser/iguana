package org.jgll.grammar.symbol;

import org.jgll.parser.HashFunctions;


public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	public Nonterminal(String name) {
		this(name, 0, false);
	}
	
	public Nonterminal(String name, int index) {
		this(name, index, false);
	}
	
	public Nonterminal(String name, int index, boolean ebnfList) {
		super(name);
		this.ebnfList = ebnfList;
		this.index = index;
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
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Nonterminal)) {
			return false;
		}
		
		Nonterminal other = (Nonterminal) obj;
		
		return name.equals(other.name) && index == other.index;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(name.hashCode(), index);
	}

	@Override
	public Symbol copy() {
		Nonterminal copy = new Nonterminal(name, index, ebnfList);
		return copy;
	}

}
