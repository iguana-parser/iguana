package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.regex.Matcher;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.unicode.UnicodeUtil;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Character extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private final int c;

	private Character(Builder builder) {
		super(builder);
		this.c = builder.c;
	}	
	
	public static Character from(int c) {
		return new Builder(c).build();
	}
	
	public int getValue() {
		return c;
	}

	@Override
	public int hashCode() {
		return c;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;

		if (!(obj instanceof Character))
			return false;
		
		Character other = (Character) obj;
		
		return c == other.c;
	}

	public static String getName(int c) {
		if(UnicodeUtil.isPrintableAscii(c)) {
			return (char) c + "";			
		} else {
			return "\\u" + String.format("%04X", c);
		}
	}
	
	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(StateType.FINAL);
		startState.addTransition(new Transition(c, finalState));
		return Automaton.builder(startState).makeDeterministic().build();
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		firstSet.add(CharacterRange.in(c, c));
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	@Override
	public Matcher getMatcher() {
		return (input, i) -> input.charAt(i) == c ? 1 : -1;
	}

	@Override
	public Matcher getBackwardsMatcher() {
		return getMatcher();
	}
	
	public static Builder builder(int c) {
		return new Builder(c);
	}
	
	@Override
	public SymbolBuilder<? extends Symbol> copyBuilder() {
		return new Builder(this);
	}

	@Override
	public String getConstructorCode() {
		return Character.class.getSimpleName() + ".builder(" + c + ")" + super.getConstructorCode() + ".build()";
	}
	
	public static class Builder extends SymbolBuilder<Character> {
		
		private int c;
		
		public Builder(int c) {
			super(getName(c));
			this.c = c; 
		}

		public Builder(Character character) {
			this(character.c);
		}
		
		@Override
		public Character build() {
			return new Character(this);
		}
	}
	
	@Override
	public boolean isSingleChar() {
		return true;
	}
	
	@Override
	public Character asSingleChar() {
		return this;
	}
	
	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public String getPattern() {
		return getName();
	}
}
