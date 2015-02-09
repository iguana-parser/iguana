package org.jgll.grammar.condition;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.Sequence;
import org.jgll.traversal.IConditionVisitor;

public class ContextFreeCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private List<? extends Symbol> symbols;
	
	public ContextFreeCondition(ConditionType type, Sequence group) {
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
	
	public static <T extends Symbol> ContextFreeCondition notMatch(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_MATCH, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition match(T...symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition match(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.MATCH, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notFollow(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition notFollow(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_FOLLOW, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition follow(T...symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition follow(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.FOLLOW, symbols);
	}

	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition notPrecede(T...symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition notPrecede(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.NOT_PRECEDE, symbols);
	}
	
	@SafeVarargs
	public static <T extends Symbol> ContextFreeCondition precede(T...symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, Arrays.asList(symbols));
	}
	
	public static <T extends Symbol> ContextFreeCondition precede(List<T> symbols) {
		return new ContextFreeCondition(ConditionType.PRECEDE, symbols);
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

	@Override
	public String getConstructorCode() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SlotAction getSlotAction() {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
