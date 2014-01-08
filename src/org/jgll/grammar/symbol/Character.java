package org.jgll.grammar.symbol;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.Automaton;
import org.jgll.regex.State;
import org.jgll.regex.Transition;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Character extends AbstractSymbol implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int c;
	
	private final BitSet bitSet;
	
	public Character(int c) {
		this.c = c;
		this.bitSet = new BitSet();
		bitSet.set(c);
	}
	
	public int getValue() {
		return c;
	}
	
	@Override
	public boolean match(int i) {
		return c == i;
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getMatchCode() {
		return "I[ci] == " + c;
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

	@Override
	public String getName() {
		return "[" + (char)c + "]";
	}

	@Override
	public BitSet asBitSet() {
		return bitSet;
	}

	@Override
	public Terminal addConditions(Collection<Condition> conditions) {
		Character terminal = new Character(this.c);
		terminal.conditions.addAll(this.conditions);
		terminal.conditions.addAll(conditions);
		return terminal;
	}
	
	@Override
	public Automaton toNFA() {
		return createNFA();
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		startState.addTransition(new Transition(c, finalState));
		return new Automaton(startState);
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Character copy() {
		return new Character(c);
	}
	
}
