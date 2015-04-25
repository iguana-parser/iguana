package org.jgll.grammar.symbol;

public enum Recursion {
	
	LEFT_REC, RIGHT_REC, LEFT_RIGHT_REC, NON_REC, UNDEFINED;
	
	public String getConstructorCode() {
		return this.getClass().getSimpleName() + "." + this.name();
	}
	
	@Override
	public String toString() {
		return this.name();
	}
}
