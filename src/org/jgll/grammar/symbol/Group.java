package org.jgll.grammar.symbol;

public abstract class Group {
	
	private final Precedence lhs;
	private final Precedence rhs;
	
	public Group(Precedence lhs, Precedence rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public Precedence getLhs() {
		return lhs;
	}
	
	public Precedence getRhs() {
		return rhs;
	}

}
