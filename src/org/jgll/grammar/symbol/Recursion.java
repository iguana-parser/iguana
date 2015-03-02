package org.jgll.grammar.symbol;

public enum Recursion {
	
	LEFT, RIGHT, LEFT_RIGHT, NON_REC;
	
	public String getConstructorCode() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Override
	public String toString() {
		return "REC(" + this.name() + ")";
	}
}
