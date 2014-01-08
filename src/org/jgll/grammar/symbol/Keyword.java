package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.Automaton;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.util.Input;

public class Keyword extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final BitSet bitSet;
	
	private final Sequence<Character> seq;
	
	public Keyword(String name, String s) {
		this(name, Input.toIntArray(s));
	}
	
	public Keyword(String name, int[] chars) {
		this(name, toCharSequence(chars));
	}
	
	private static Sequence<Character> toCharSequence(int[] chars) {
		List<Character> list = new ArrayList<>();
		for(int c : chars) {
			list.add(new Character(c));
		}
		
		return new Sequence<>(list);		
	}
	
	public Keyword(String name, Sequence<Character> seq) {
		this.name = name;
		this.seq = seq;
		
		this.bitSet = new BitSet();
		bitSet.set(seq.get(0).getValue());		
	}
	
	public int size() {
		return seq.size();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Terminal getFirstTerminal() {
		return (Character) seq.get(0);
	}
	
	public Sequence<Character> getSequence() {
		return seq;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Keyword)) {
			return false;
		}
		
		Keyword other = (Keyword) obj;
		
		return seq.equals(other.seq);
	}
	
	@Override
	public int hashCode() {
		return seq.hashCode();
	}
	
	@Override
	public Keyword addConditions(Collection<Condition> conditions) {
		Keyword keyword = new Keyword(this.name, this.seq);
		keyword.conditions.addAll(this.conditions);
		keyword.conditions.addAll(conditions);
		return keyword;
	}

	@Override
	public BitSet asBitSet() {
		BitSet set = new BitSet();
		set.set(seq.get(0).getValue());
		return set;
	}
	
	private Automaton createNFA() {
		return seq.toNFA();
	}
	
	@Override
	public Automaton toNFA() {
		return createNFA();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public RegularExpression copy() {
		return new Keyword(name, seq);
	}
	
}
