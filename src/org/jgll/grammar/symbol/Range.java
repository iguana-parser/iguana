package org.jgll.grammar.symbol;

import static org.jgll.regex.automaton.TransitionActionsFactory.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.State;
import org.jgll.regex.automaton.StateType;
import org.jgll.regex.automaton.Transition;
import org.jgll.util.CollectionsUtil;


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
		this(start, end, Collections.<Condition>emptySet());
	}
	
	public Range(int start, int end, Set<Condition> conditions) {
		this(getName(start, end), start, end, conditions, null);
	}
		
	public Range(String name, int start, int end, Set<Condition> conditions, Object object) {
		super(name, conditions, object);
		
		if(end < start) throw new IllegalArgumentException("Start cannot be less than end.");
		
		this.start = start;
		this.end = end;
	}

	private static String getName(int start, int end) {
		return Character.getName(start) + "-" + Character.getName(end);
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public boolean contains(Range other) {
		return start <= other.start && end >= other.end;
	}
	
	public boolean overlaps(Range other) {
		return end > other.start || other.end > start;
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
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(StateType.FINAL);
		startState.addTransition(new Transition(start, end, finalState).addTransitionAction(getPostActions(conditions)));
		return new Automaton(startState, name);
	}

	@Override
	public boolean isNullable() {
		return false;
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
	
	@Override
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}

	@Override
	public Range withConditions(Set<Condition> conditions) {
		return new Range(start, end, CollectionsUtil.union(conditions, this.conditions));
	}
	
	@Override
	public Range withoutConditions() {
		return new Range(start, end);
	}
	
}
