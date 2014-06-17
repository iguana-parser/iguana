package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
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

	public Character(int c, String label, Set<Condition> conditions, Object object) {
		super(getName(c), label, conditions, object);
		this.c = c;
	}	
	
	public static Character from(int c) {
		return new Character(c, null, Collections.<Condition>emptySet(), null);
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
		List<Range> ranges = new ArrayList<>();
		if(c >= 1) {
			ranges.add(Range.in(1, c - 1));
		}
		if(c < Constants.MAX_UTF32_VAL) {
			ranges.add(Range.in(c + 1, Constants.MAX_UTF32_VAL));
		}
		CharacterClass c = CharacterClass.from(ranges);
		return c;
	}

	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		firstSet.add(Range.in(c, c));
		return firstSet;
	}
	
	@Override
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}

	public static class Builder extends SymbolBuilder<Character> {
		
		private int c;
		
		public Builder(int c) { this.c = c; }

		public Builder(Character character) {
			super(character);
			this.c = character.c;
		}
		
		@Override
		public Character build() {
			return new Character(c, label, conditions, object);
		}
	}

	@Override
	public SymbolBuilder<Character> builder() {
		return new Builder(this);
	}

}
