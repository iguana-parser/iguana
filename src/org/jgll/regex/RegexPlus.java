package org.jgll.regex;

import java.util.BitSet;


public class RegexPlus implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final RegularExpression regexp;
	
	public RegexPlus(RegularExpression regexp) {
		this.regexp = new Sequence(regexp, new RegexStar(regexp));
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

}
