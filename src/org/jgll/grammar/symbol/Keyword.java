package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.regex.Matcher;
import org.jgll.regex.Sequence;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;

public class Keyword extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final Sequence<Character> seq;
	
	public static Keyword from(String s) {
		return from(Input.toIntArray(s));
	}
	
	public static Keyword from(int[] chars) {
		return new Builder(toCharSequence(chars)).build();
	}
	
	private Keyword(Builder builder) {
		super(builder);
		this.seq = builder.seq;
	}
	
	private static Sequence<Character> toCharSequence(int[] chars) {
		List<Character> list = new ArrayList<>();
		for(int c : chars) {
			list.add(Character.from(c));
		}
		
		return Sequence.from(list);
	}
		
	public int size() {
		return seq.size();
	}
	
	public Sequence<Character> getSequence() {
		return seq;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof Keyword))
			return false;
		
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
	public Set<CharacterRange> getFirstSet() {
		return seq.getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	/**
	 * 
	 * Creates the corresponding grammar rule for this keyword.
	 * For example, for the keyword "if", a rule If ::= [i][f]
	 * is returned.
	 * 
	 */
	public Rule toRule() {
		Rule.Builder builder = Rule.builder(Nonterminal.withName(name));
		seq.forEach(c -> builder.addSymbol(c));
		return builder.build();
	}
	
	public static Builder builder(List<Character> seq) {
		return new Builder(Sequence.from(seq));
	}
	
	public static Builder builder(Sequence<Character> seq) {
		return new Builder(seq);
	}
	
	public static Builder builder(String s) {
		return new Builder(toCharSequence(Input.toIntArray(s)));
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new Keyword(")
		  .append(seq.getConstructorCode(registry) + ", ")
		  .append(label + ", ")
		  .append("null")
		  .append(")");
		return sb.toString();
	}
	
	@Override
	public Matcher getMatcher() {
		return (input, i) -> {
			int length = -1;
			for (Character c : seq) {
				if (c.getValue() == input.charAt(i++)) {
					length++;
				}
			}
			return length;
		};
	}

	
	public static class Builder extends SymbolBuilder<Keyword> {
		
		private Sequence<Character> seq;
				
		public Builder(String s) {
			super(toCharSequence(Input.toIntArray(s)).getName());
			this.seq = toCharSequence(Input.toIntArray(s));
		}
		
		public Builder(Sequence<Character> seq) {
			super(seq.getName());
			this.seq = seq;
		}
		
		public Builder(Keyword keyword) {
			super(keyword);
			this.seq = keyword.seq;
		}

		@Override
		public Keyword build() {
			return new Keyword(this);
		}
	}

}
