package org.jgll.grammar;

public class Nonterminal implements Symbol {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final boolean ebnfList;
	
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
		return ebnfList;
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
		int result = 17;
		result = 31 * result + name.hashCode();
		return result;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

	@Override
	public boolean isNonterminal() {
		return true;
	}
}
