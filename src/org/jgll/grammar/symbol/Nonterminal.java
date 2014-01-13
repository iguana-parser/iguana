package org.jgll.grammar.symbol;


public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private boolean collapsible;
	
	public Nonterminal(String name) {
		this(name, false);
	}

	public Nonterminal(String name, boolean ebnfList) {
		super(name);
		this.ebnfList = ebnfList;
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
		Nonterminal copy = new Nonterminal(name, ebnfList);
		copy.collapsible = collapsible;
		return copy;
	}

}
