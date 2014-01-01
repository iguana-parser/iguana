package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.NFA;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;
import org.jgll.regex.Transition;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractSymbol implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final List<Range> ranges;
	
	private BitSet testSet;
	
	public CharacterClass(Range...ranges) {
		this(Arrays.asList(ranges));
	}
	
	public CharacterClass(List<Range> ranges) {
		
		if(ranges == null || ranges.size() == 0) {
			throw new IllegalArgumentException("Ranges cannot be null or empty.");
		}
		
		testSet = new BitSet();
		
		for(Range range : ranges) {
			testSet.or(range.asBitSet());
		}
		
		this.ranges = Collections.unmodifiableList(ranges);
	}
	
	public List<Range> getRanges() {
		return ranges;
	}
	
	@Override
	public boolean match(int i) {
		return testSet.get(i);
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getMatchCode() {
		StringBuilder sb = new StringBuilder();
		for(Range range : ranges) {
			sb.append(range.getMatchCode()).append(" || ");
		}
		return sb.toString();
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

		return testSet.equals(other.testSet);
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
		return testSet;
	}
	
	@Override
	public Terminal addConditions(Collection<Condition> conditions) {
		CharacterClass characterClass = new CharacterClass(this.ranges);
		characterClass.conditions.addAll(this.conditions);
		characterClass.conditions.addAll(conditions);
		return characterClass;
	}
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		for(Range range : ranges) {
			NFA nfa = range.toNFA();
			startState.addTransition(Transition.emptyTransition(nfa.getStartState()));
			nfa.getEndState().setFinalState(false);
			nfa.getEndState().addTransition(Transition.emptyTransition(finalState));
		}
		
		return new NFA(startState, finalState);
	}
	
	@Override
	public NFA toNFA() {
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