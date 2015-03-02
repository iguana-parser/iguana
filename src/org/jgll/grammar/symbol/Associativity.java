package org.jgll.grammar.symbol;

public enum Associativity {
	
	LEFT, RIGHT, NON_ASSOC, UNDEFINED;
		
	public String getConstructorCode() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
}
