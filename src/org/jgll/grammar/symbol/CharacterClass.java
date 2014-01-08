package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.Automaton;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;
import org.jgll.regex.Transition;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * TODO: CharacterClass is an RegexAlt of character classes. Rewrite it.
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractSymbol implements RegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private RegexAlt<Range> ranges;
	
	public CharacterClass(Range...ranges) {
		this(Arrays.asList(ranges));
	}
	
	public CharacterClass(List<Range> ranges) {
		this(new RegexAlt<>(ranges));
	}
	
	public CharacterClass(RegexAlt<Range> ranges) {
		this.ranges = ranges;
	}
	
	
	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns true the provided character class is a subset of
	 * this character class.
	 */
	public boolean contains(CharacterClass other) {
		// For each range of the other character class, find a 
		// range that contains it
		for(Range otherRange : other.ranges) {
			boolean contains = false;
			for(Range range : ranges) {
				if(range.contains(otherRange)) {
					contains = true;
				}
			}
			if(!contains) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return ranges.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CharacterClass)) {
			return false;
		}
		
		CharacterClass other = (CharacterClass) obj;

		return ranges.equals(other.ranges);
	}

	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(Range range : ranges) {
			sb.append(getChar(range.getStart())).append("-").append(getChar(range.getEnd()));
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String getChar(int val) {
		char c = (char) val;
		if(c == '-' || c == ' ') {
			return "\\" + c;
		}
		
		// Escaping specific character class symbols
		if(c == '-') {
			return "\\-";
		}
		if(c == '[') {
			return "\\[";
		}
		if(c == ']') {
			return "\\]";
		}
		if(c == '#') {
			return "\\#";
		}
		if(c == '.') {
			return "\\.";
		}
		if(c == '\\') {
			return "\\\\";
		}
			
		return new String(java.lang.Character.toChars(val));
	}
	
	@Override
	public BitSet asBitSet() {
		return ranges.asBitSet();
	}
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions) {
		CharacterClass characterClass = new CharacterClass(this.ranges);
		characterClass.conditions.addAll(this.conditions);
		characterClass.conditions.addAll(conditions);
		return characterClass;
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		for(Range range : ranges) {
			Automaton nfa = range.toNFA();
			startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
			
			Set<State> finalStates = nfa.getFinalStates();
			for(State s : finalStates) {
				s.setFinalState(false);
				s.addTransition(Transition.emptyTransition(finalState));
			}
		}
		
		return new Automaton(startState);
	}
	
	@Override
	public Automaton toNFA() {
		return createNFA();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public RegularExpression copy() {
		List<Range> copyRanges = new ArrayList<>();
		for(Range range : ranges) {
			copyRanges.add(range.copy());
		}
		return new CharacterClass(copyRanges);
	}
	
}