package org.jgll.regex;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Symbol;


public class RegexPlus extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexPlus(RegularExpression regexp) {
		this.regexp = new Sequence(regexp, new RegexStar(regexp.copy()));
	}
	
	@Override
	public NFA toNFA() {
		return regexp.toNFA();
	}
	
	@Override
	public boolean isNullable() {
		return regexp.isNullable();
	}
	
	@Override
	public BitSet asBitSet() {
		return regexp.asBitSet();
	}

	@Override
	public String getName() {
		return regexp.getName() + "+";
	}

	@Override
	public Symbol addConditions(Collection<Condition> conditions) {
		return null;
	}

	@Override
	public RegexPlus copy() {
		return new RegexPlus(regexp.copy());
	}
}
