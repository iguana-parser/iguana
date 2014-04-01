package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.parser.HashFunctions;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.Transition;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range extends AbstractRegularExpression implements Comparable<Range> {
	
	private static final long serialVersionUID = 1L;
	
	private static final int MAX_UTF32_VAL = 0x10FFFF;

	private final int start;
	
	private final int end;
	
	public static Range in(int start, int end) {
		return new Range(start, end);
	}
		
	public Range(int start, int end) {
		super(Character.getString(start) + "-" + Character.getString(end));
		
		if(end < start) {
			throw new IllegalArgumentException("Start cannot be less than end.");
		}
		
		this.start = start;
		this.end = end;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
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

	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true).addActions(actions).addRegularExpression(this);
		startState.addTransition(new Transition(start, end, finalState));
		return new Automaton(startState);
	}

	@Override
	public Automaton toAutomaton() {
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
		if(start >= 1) {
			ranges.add(Range.in(1, start - 1));
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

	@Override
	public Set<Range> getFirstSet() {
		Set<Range> firstSet = new HashSet<>();
		firstSet.add(this);
		return firstSet;
	}
}
