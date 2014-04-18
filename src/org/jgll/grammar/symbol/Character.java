package org.jgll.grammar.symbol;

import static org.jgll.regex.automaton.TransitionActionsFactory.getPostActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;
/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Character extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private final int c;
	
	public Character(int c, Set<Condition> conditions) {
		super(getString(c), conditions);
		this.c = c;
	}
	
	public Character(int c) {
		this(c, Collections.<Condition>emptySet());
	}
	
	public static Character from(int c) {
		return new Character(c);
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

	public static String getString(int c) {
		if(c >= Constants.FIRST_PRINTABLE_CHAR && c <= java.lang.Character.MAX_VALUE) {
			return (char)c + "";			
		} else {
			return "\\u" + Integer.toHexString(c);
		}
	}
	
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(true).addRegularExpression(this);
		Transition transition = new Transition(c, finalState).addTransitionAction(getPostActions(conditions));
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
		CharacterClass c = new CharacterClass(ranges);
		return c;
	}

	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		firstSet.add(new Range(c, c));
		return firstSet;
	}

	@Override
	public Character withConditions(Set<Condition> conditions) {
		return new Character(c, conditions);
	}

}
