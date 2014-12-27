package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
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
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Character)) {
			return false;
		}
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
		Transition transition = new Transition(c, finalState); //.addTransitionAction(getPostActions(conditions));
		startState.addTransition(transition);
		return new Automaton(startState, name);
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	public CharacterClass not() {
		List<CharacterRange> ranges = new ArrayList<>();
		if(c >= 1) {
			ranges.add(CharacterRange.in(1, c - 1));
		}
		if(c < Constants.MAX_UTF32_VAL) {
			ranges.add(CharacterRange.in(c + 1, Constants.MAX_UTF32_VAL));
		}
		CharacterClass c = CharacterClass.from(ranges);
		return c;
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

	public static class Builder extends SymbolBuilder<Character> {
		
		private int c;
		
		public Builder(int c) {
			super(getName(c));
			this.c = c; 
		}

		public Builder(Character character) {
			super(character);
			this.c = character.c;
		}
		
		@Override
		public Character build() {
			return new Character(this);
		}
	}

	public static Builder builder(int c) {
		return new Builder(c);
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "Character.from(" + c + ")";
	}

}
