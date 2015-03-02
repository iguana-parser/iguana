package org.jgll.grammar.symbol;

public abstract class Group {
	
	private final int lhs;
	private int rhs;
	
	public Group(int lhs, int rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public void setRhs(int level) {
		if (rhs == -1)
			rhs = level;
		else
			throw new RuntimeException("Should not reset the upper bound when it is not -1!");
	}
	
	public abstract String getConstructorCode();

}
