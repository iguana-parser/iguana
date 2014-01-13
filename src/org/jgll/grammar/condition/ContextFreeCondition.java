package org.jgll.grammar.condition;

import static org.jgll.util.CollectionsUtil.listToString;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Symbol;

public class ContextFreeCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private List<? extends Symbol> symbols;
	
	public ContextFreeCondition(ConditionType type, Group group) {
		super(type);
		this.symbols = group.getSymbols();
	}
	
	public ContextFreeCondition(ConditionType type, List<? extends Symbol> symbols) {
		super(type);
		this.symbols = symbols;
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notMatch(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, Arrays.asList(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition match(T...symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, Arrays.asList(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notFollow(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, Arrays.asList(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition follow(T...symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, Arrays.asList(symbols));
	}

	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notPrecede(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, Arrays.asList(symbols));
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition precede(T...symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, Arrays.asList(symbols));
	}
	
	public List<? extends Symbol> getSymbols() {
		return symbols;
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + listToString(symbols);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof ContextFreeCondition)) {
			return false;
		}
		
		ContextFreeCondition other = (ContextFreeCondition) obj;
		
		return type == other.type && symbols.equals(other.symbols);
	}
}
