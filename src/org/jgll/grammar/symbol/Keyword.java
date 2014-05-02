package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.Sequence;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.CollectionsUtil;
import org.jgll.util.Input;

public class Keyword extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Sequence<Character> seq;
	
	public Keyword(String s) {
		this(s, s);
	}
	
	public Keyword(String name, String s) {
		this(name, Input.toIntArray(s));
	}
	
	public Keyword(String name, int[] chars) {
		this(name, toCharSequence(chars));
	}
	
	public Keyword(String name, Sequence<Character> seq) {
		this(name, seq, Collections.<Condition>emptySet());
	}
	
	public Keyword(String name, Sequence<Character> seq, Set<Condition> conditions) {
		super(name, conditions);
		this.name = name;
		this.seq = seq.withConditions(conditions);
	}
	
	private static Sequence<Character> toCharSequence(int[] chars) {
		List<Character> list = new ArrayList<>();
		for(int c : chars) {
			list.add(new Character(c));
		}
		
		return new Sequence<>(list);		
	}
		
	public int size() {
		return seq.size();
	}
	
	public Sequence<Character> getSequence() {
		return seq;
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
	protected Automaton createAutomaton() {
		return seq.getAutomaton();
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Set<Range> getFirstSet() {
		return seq.getFirstSet();
	}

	@Override
	public Keyword withConditions(Set<Condition> conditions) {
		return new Keyword(name, seq, CollectionsUtil.union(conditions, this.conditions));
	}
	
	@Override
	public Keyword withCondition(Condition condition) {
		return (Keyword) super.withCondition(condition);
	}
	
	@Override
	public Keyword withoutConditions() {
		return new Keyword(name, seq);
	}
	
}
