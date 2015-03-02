package org.jgll.grammar.symbol;

public class AssociativityGroup extends Group {
	
	private final Associativity associativity;

	public AssociativityGroup(Associativity associativity, int lhs, int rhs) {
		super(lhs, rhs);
		this.associativity = associativity;
	}
	
	public AssociativityGroup(Associativity associativity, int lhs) {
		super(lhs, -1);
		this.associativity = associativity;
	}
	
	public Associativity getAssociativity() {
		return associativity;
	}
	
	@Override
	public String getConstructorCode() {
		return "new " + getClass().getSimpleName() + "(" + associativity.getConstructorCode() + "," + getLhs() + "," + getRhs() + ")";
	}
	
	@Override
	public String toString() {
		return associativity.name() + "(" + getLhs() + "," + getRhs() + ")";
	}

}
