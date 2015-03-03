package org.jgll.grammar.symbol;

public class PrecedenceLevel {
	
	private final int lhs;
	private int rhs = -1;
	
	private boolean hasUnary = false;
	private boolean hasUnaryBelow = false;
	
	private int undefined = -1;
	
	private int index;
	
	PrecedenceLevel(int lhs) {
		this.lhs = lhs;
		this.index = lhs;
	}
	
	public static PrecedenceLevel from(int lhs) {
		return new PrecedenceLevel(lhs);
	}
	
	public PrecedenceLevel getNext() {
		this.done();
		
		PrecedenceLevel next = new PrecedenceLevel(index);
		
		if (hasUnary || hasUnaryBelow)
			next.hasUnaryBelow = true;
		
		return next;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public int getPrecedence(Rule rule) {
		
		if (rule.isUnary()) hasUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		
		else if (rule.getAssociativity() == Associativity.UNDEFINED) {	
			if (undefined == -1) undefined = index++;
			return undefined;
		} else
			return index++;
	}
	
	public boolean isUndefined(int precedence) {
		return this.undefined == precedence;
	}
	
	public void done() {
		assert rhs != -1;
		rhs = index == 0? index : index - 1;
	}
		
	public String getConstructorCode() {
		return "new " + getClass().getSimpleName() + "(" + lhs + "," + rhs + ")";
	}
	
	@Override
	public String toString() {
		return "(" + lhs + "," + rhs + ")";
	}
}
