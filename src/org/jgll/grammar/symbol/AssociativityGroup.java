package org.jgll.grammar.symbol;

import java.util.HashMap;
import java.util.Map;


public class AssociativityGroup extends Group {
	
	private final Associativity associativity;
	
	private final Map<Integer, Associativity> map;

	public AssociativityGroup(Associativity associativity, int lhs, int rhs) {
		super(lhs, rhs);
		this.associativity = associativity;
		this.map = new HashMap<>();
	}
	
	public AssociativityGroup(Associativity associativity, int lhs) {
		this(associativity, lhs, -1);
	}
	
	public void add(int precedence, Associativity associativity) {
		if (precedence == getLhs()) {
			if (this.associativity == associativity)
				return;
			throw new RuntimeException("Unexpected pair of a precedence level and associativity: " + precedence + "," + associativity);
		}
		
		map.put(precedence, associativity);
	}
	
	public Associativity get(int precedence) {
		if (precedence == getLhs())
			return associativity;
		
		Associativity result = map.get(precedence);
		
		if (result == null)
			throw new RuntimeException("Unexpected precedence level: " + precedence);
		
		return result;
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
