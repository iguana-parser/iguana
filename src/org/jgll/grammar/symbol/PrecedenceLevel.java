package org.jgll.grammar.symbol;

public class PrecedenceLevel {
	
	private final int lhs;
	private int rhs = -1;
	
	private boolean hasPrefixUnary = false;
	private boolean hasPrefixUnaryBelow = false;
	
	private boolean hasPostfixUnary = false;
	private boolean hasPostfixUnaryBelow = false;
	
	private int undefined = -1;
	
	private int index;
	
	PrecedenceLevel(int lhs) {
		this.lhs = lhs;
		this.index = lhs;
	}
	
	public static PrecedenceLevel from(int lhs, int rhs, int undefined, boolean hasPrefixUnary, boolean hasPostfixUnary, 
										boolean hasPrefixUnaryBelow, boolean hasPostfixUnaryBelow) {
		PrecedenceLevel level = new PrecedenceLevel(lhs);
		level.rhs = rhs;
		level.undefined = undefined;
		level.hasPrefixUnary = hasPrefixUnary;
		level.hasPostfixUnary = hasPostfixUnary;
		level.hasPrefixUnaryBelow = hasPrefixUnaryBelow;
		level.hasPostfixUnaryBelow = hasPostfixUnaryBelow;
		return level;
	}
	
	public static PrecedenceLevel getFirst() {
		return new PrecedenceLevel(1);
	}
	
	public static PrecedenceLevel getFirstAndDone() {
		PrecedenceLevel level = new PrecedenceLevel(1);
		level.done();
		return level;
	}
	
	public PrecedenceLevel getNext() {
		this.done();
		
		PrecedenceLevel next = new PrecedenceLevel(index);
		
		if (hasPrefixUnary || hasPrefixUnaryBelow)
			next.hasPrefixUnaryBelow = true;
		
		if (hasPostfixUnary || hasPostfixUnaryBelow)
			next.hasPostfixUnaryBelow = true;
		
		return next;
	}
	
	public int getLhs() {
		return lhs;
	}
	
	public int getRhs() {
		return rhs;
	}
	
	public boolean hasPrefixUnary() {
		return hasPrefixUnary;
	}
	
	public boolean hasPostfixUnary() {
		return hasPostfixUnary;
	}
	
	public boolean hasPrefixUnaryBelow() {
		return hasPrefixUnaryBelow;
	}
	
	public boolean hasPostfixUnaryBelow() {
		return hasPostfixUnaryBelow;
	}
	
	public int getPrecedence(Rule rule) {
		
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		else if (rule.getAssociativity() == Associativity.UNDEFINED) {
			if (undefined == -1) {
				if (lhs == 1)
					undefined = 0;
				else 
					undefined = index++;
			}
			return undefined;
		} else
			return index++;
	}
	
	int getPrecedenceFromAssociativityGroup(Rule rule) {
		if (rule.isUnary() && rule.isRightRecursive()) hasPrefixUnary = true;
		if (rule.isUnary() && rule.isLeftRecursive()) hasPostfixUnary = true;
		
		if (!rule.isLeftOrRightRecursive()) return -1;
		else return index++;
	}
	
	public void setUndefinedIfNeeded() {
		if (undefined == -1) {
			int rhs = index == lhs? index : index - 1;
			if (lhs != rhs) {
				if (lhs == 1)
					undefined = 0;
				else 
					undefined = index++;
			}
		} 
	}
	
	public boolean isUndefined(int precedence) {
		return this.undefined != -1 && this.undefined == precedence;
	}
	
	public int getUndefined() {
		return undefined;
	}
	
	public void done() {
		assert rhs != -1;
		rhs = index == lhs? index : index - 1;
	}
	
	int getCurrent() {
		return index == lhs? index : index - 1;
	}
		
	public String getConstructorCode() {
		return getClass().getSimpleName() + ".from(" + lhs + "," + rhs + "," + undefined + "," + hasPrefixUnary + "," + hasPostfixUnary + "," 
														+ hasPrefixUnaryBelow + "," + hasPostfixUnaryBelow + ")";
	}
	
	@Override
	public String toString() {
		return "PREC(" + lhs + "," + rhs + ")";
	}
}
