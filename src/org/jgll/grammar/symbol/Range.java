package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.Automaton;
import org.jgll.regex.State;
import org.jgll.regex.Transition;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range extends AbstractSymbol implements Terminal, Comparable<Range> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_UTF32_VAL = 0x10FFFF;

	private final int start;
	
	private final int end;
	
	private final BitSet testSet;
	
	public static Range in(int start, int end) {
		return new Range(start, end);
	}
	
	public Range(int start, int end) {
		
		if(end < start) {
			throw new IllegalArgumentException("Start cannot be less than end.");
		}
		
		this.start = start;
		this.end = end;
		
		testSet = new BitSet();
		testSet.set(start, end + 1);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
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
		return "(I[ci] >= " + start + " && I[ci] <= " + end + ")";
	}
	
	public boolean contains(Range range) {
		return start <= range.start && end >= range.end;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(start, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Range)) {
			return false;
		}
		
		Range other = (Range) obj;
		
		return start == other.start && end == other.end;
	}

	@Override
	public String getName() {
		return  "[" + Character.getString(start) + "-" + Character.getString(end) + "]";
	}

	@Override
	public BitSet asBitSet() {
		return testSet;
	}
	
	@Override
	public Terminal addConditions(Collection<Condition> conditions) {
		Range range = new Range(this.start, this.end);
		range.conditions.addAll(this.conditions);
		range.conditions.addAll(conditions);
		return range;
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		startState.addTransition(new Transition(start, end, finalState));
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
	public Range copy() {
		return new Range(start, end);
	}

	public CharacterClass not() {
		List<Range> ranges = new ArrayList<>();
		if(start > 0) {
			ranges.add(Range.in(0, start - 1));
		}
		if(end < MAX_UTF32_VAL) {
			ranges.add(Range.in(end + 1, MAX_UTF32_VAL));
		}
		CharacterClass c = new CharacterClass(ranges);
		return c;
	}

	@Override
	public int compareTo(Range o) {
		return start - o.start;
	}
}
