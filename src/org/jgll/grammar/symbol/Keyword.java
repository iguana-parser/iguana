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
		this(Input.toIntArray(s));
	}
	
	public Keyword(int[] chars) {
		this(toCharSequence(chars));
	}
	
	public Keyword(Sequence<Character> seq) {
		this(seq, Collections.<Condition>emptySet(), null);
	}
	
	public Keyword(Sequence<Character> seq, Set<Condition> conditions, Object object) {
		super(seq.getName(), conditions, object);
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
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public Keyword withConditions(Set<Condition> conditions) {
		return new Keyword(seq, CollectionsUtil.union(conditions, this.conditions), null);
	}
	
	@Override
	public Keyword withCondition(Condition condition) {
		return (Keyword) super.withCondition(condition);
	}
	
	@Override
	public Keyword withoutConditions() {
		return new Keyword(seq);
	}
	
}
