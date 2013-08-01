package org.jgll.grammar;

import java.util.BitSet;

import org.jgll.grammar.condition.Condition;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Character extends AbstractSymbol implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int c;

	public Character(int c) {
		this.c = c;
	}
	
	public int get() {
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
		BitSet set = new BitSet();
		set.set(c);
		return set;
	}

	@Override
	public Terminal addCondition(Condition condition) {
		Character terminal = new Character(this.c);
		terminal.conditions.addAll(conditions);
		terminal.conditions.add(condition);
		return terminal;
	}

}
