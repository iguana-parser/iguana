package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgll.regex.Sequence;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;

public class Keyword extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final String name;
	
	private final Sequence<Character> seq;
	
	public Keyword(String s) {
		this("no-name", s);
	}
	
	public Keyword(String name, String s) {
		this(name, Input.toIntArray(s));
	}
	
	public Keyword(String name, int[] chars) {
		this(name, toCharSequence(chars));
	}
	
	public Keyword(String name, Sequence<Character> seq) {
		super(name);
		this.name = name;
		this.seq = seq;
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
	
	private Automaton createAutomaton() {
		return seq.toAutomaton().addFinalStateActions(actions).addRegularExpression(this);
	}
	
	@Override
	public Automaton toAutomaton() {
		return combineConditions(createAutomaton());
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Keyword copy() {
		return new Keyword(name, seq);
	}

	@Override
	public Set<Range> getFirstSet() {
		return seq.getFirstSet();
	}
	
}
