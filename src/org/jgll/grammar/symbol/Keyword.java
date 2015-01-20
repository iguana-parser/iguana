package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jgll.regex.Group;
import org.jgll.regex.Matcher;
import org.jgll.regex.automaton.Automaton;
import org.jgll.util.Input;

public class Keyword extends AbstractRegularExpression {

	private static final long serialVersionUID = 1L;
	
	private final List<Character> characters;
	
	public static Keyword from(String s) {
		return from(Input.toIntArray(s));
	}
	
	public static Keyword from(int[] chars) {
		return new Builder(toCharSequence(chars)).build();
	}
	
	private Keyword(Builder builder) {
		super(builder);
		this.characters = builder.seq;
	}
	
	private static List<Character> toCharSequence(int[] chars) {
		return Arrays.stream(chars).mapToObj(Character::from).collect(Collectors.toList());
	}
		
	public int size() {
		return characters.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		if(!(obj instanceof Keyword))
			return false;
		
		Keyword other = (Keyword) obj;
		
		return characters.equals(other.characters);
	}
	
	@Override
	public int hashCode() {
		return characters.hashCode();
	}
	
	@Override
	protected Automaton createAutomaton() {
		return Group.from(characters).getAutomaton();
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		return characters.get(0).getFirstSet();
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
		Rule.Builder builder = Rule.withHead(Nonterminal.withName(name));
		characters.forEach(c -> builder.addSymbol(c));
		return builder.build();
	}
	
	public static Builder builder(List<Character> seq) {
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
	public String getConstructorCode() {
		return Keyword.class.getSimpleName() + ".builder()" + ".build()";
	}
	
	@Override
	public Matcher getMatcher() {
		return (input, i) -> {
			for (Character c : characters) {
				if (c.getValue() != input.charAt(i++)) {
					return -1;
				}
			}
			return characters.size();
		};
	}
	
	private static String getName(List<Character> seq) {
		return "\"" + seq.stream().map(c -> c.getName()).collect(Collectors.joining()) + "\"";
	}
	
	public static class Builder extends SymbolBuilder<Keyword> {
		
		private List<Character> seq;
				
		public Builder(String s) {
			super(s);
			this.seq = toCharSequence(Input.toIntArray(s));
		}
		
		public Builder(List<Character> seq) {
			super(getName(seq));
			this.seq = seq;
		}
		
		public Builder(Keyword keyword) {
			this(keyword.characters);
		}

		@Override
		public Keyword build() {
			return new Keyword(this);
		}
	}

	@Override
	public Pattern getPattern() {
		throw new UnsupportedOperationException();
	}

}
