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
	
	public static PrecedenceLevel from(int lhs, int rhs, int undefined, boolean hasUnaryBelow) {
		PrecedenceLevel level = PrecedenceLevel.from(lhs);
		level.rhs = rhs;
		level.undefined = undefined;
		level.hasUnaryBelow = hasUnaryBelow;
		return level;
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
	
	public boolean hasUnaryBelow() {
		return hasUnaryBelow;
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
	
	int getPrecedenceFromAssociativityGroup(Rule rule) {
		if (rule.isUnary()) hasUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		else return index++;
	}
	
	public boolean isUndefined(int precedence) {
		return this.undefined != -1 && this.undefined == precedence;
	}
	
	public void done() {
		assert rhs != -1;
		rhs = index == 0? index : index - 1;
	}
	
	int getCurrent() {
		return index == 0? index : index - 1;
	}
		
	public String getConstructorCode() {
		return getClass().getSimpleName() + "from(" + lhs + "," + rhs + "," + undefined + "," + hasUnaryBelow + ")";
	}
	
	@Override
	public String toString() {
		return "PREC(" + lhs + "," + rhs + ")";
	}
}
