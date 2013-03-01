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

}
