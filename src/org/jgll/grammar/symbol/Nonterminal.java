package org.jgll.grammar.symbol;


public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	public Nonterminal(String name) {
		this(name, 0, false);
	}
	
	public Nonterminal(String name, boolean ebnfList) {
		this(name, 0, ebnfList);
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
	public String getName() {
		return index > 0 ? name + index : name;
	}
	
	@Override
	public String toString() {
		return getName();
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

	@Override
	public Symbol copy() {
		Nonterminal copy = new Nonterminal(name, index, ebnfList);
		return copy;
	}

}
