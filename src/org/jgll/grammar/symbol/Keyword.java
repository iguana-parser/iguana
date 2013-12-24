package org.jgll.grammar.symbol;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.NFA;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;
import org.jgll.regex.Transition;
import org.jgll.util.Input;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class Keyword extends AbstractSymbol implements Token, RegularExpression {

	private static final long serialVersionUID = 1L;
	
	public static final ExternalHasher<Keyword> externalHasher = new KeywordExternalHasher();
	
	private final int[] chars;
	
	private final String name;
	
	private final BitSet bitSet;
	
	private final NFA nfa;
	
	public Keyword(String name, String s) {
		this(name, Input.toIntArray(s));
	}
	
	public Keyword(String name, int[] chars) {
		this.chars = chars;
		this.name = name;
		this.bitSet = new BitSet();
		this.nfa = createNFA();
		
		bitSet.set(chars[0]);
	}
	
	public int[] getChars() {
		return chars;
	}

	public int size() {
		return chars.length;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Terminal getFirstTerminal() {
		return new Character(chars[0]);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * 
	 * Checks if this keyword matches a prefix of the given string from the provided index.
	 * 
	 * @param s
	 * @param index
	 * @return
	 */
	public boolean match(String s, int index) {
		for(int i = 0; i < chars.length; i++) {
			if(chars[i] != s.charAt(i + index)) {
				return false;
			}
		}
		
		return true;
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
		
		return Arrays.equals(chars, other.chars);
	}
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	public static class KeywordExternalHasher implements ExternalHasher<Keyword> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(Keyword k, HashFunction f) {
			return f.hash(k.getChars());
		}

		@Override
		public boolean equals(Keyword k1, Keyword k2) {
			return Arrays.equals(k1.chars, k2.chars);
		}
	}

	@Override
	public Keyword addConditions(Collection<Condition> conditions) {
		Keyword keyword = new Keyword(this.name, this.chars);
		keyword.conditions.addAll(this.conditions);
		keyword.conditions.addAll(conditions);
		return keyword;
	}

	@Override
	public BitSet asBitSet() {
		BitSet set = new BitSet();
		set.set(chars[0]);
		return set;
	}
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		State currenState = startState;
		
		for(int c : chars) {
			State nextState = new State();
			currenState.addTransition(new Transition(c, nextState));
			currenState = nextState;
		}

		currenState.addTransition(Transition.emptyTransition(finalState));
		
		return new NFA(startState, finalState);
	}
	
	@Override
	public NFA toNFA() {
		return nfa;
	}

	
}
