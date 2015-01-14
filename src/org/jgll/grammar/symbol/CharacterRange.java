package org.jgll.grammar.symbol;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgll.parser.HashFunctions;
import org.jgll.regex.Matcher;
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
public class CharacterRange extends AbstractRegularExpression implements Comparable<CharacterRange> {
	
	private static final long serialVersionUID = 1L;
	
	private final int start;
	
	private final int end;
	
	public static CharacterRange in(int start, int end) {
		return new Builder(start, end).build();
	}
		
	private CharacterRange(Builder builder) {
		super(builder);
		
		if(builder.end < builder.start) throw new IllegalArgumentException("Start cannot be less than end.");
		
		this.start = builder.start;
		this.end = builder.end;
	}

	private static String getName(int start, int end) {
		if (start == end) {
			return Character.getName(start);
		} else {
			return Character.getName(start) + "-" + Character.getName(end);			
		}
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public boolean contains(CharacterRange other) {
		return start <= other.start && end >= other.end;
	}
	
	public boolean overlaps(CharacterRange other) {
		return end > other.start || other.end > start;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(start, end);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		
		if (!(obj instanceof CharacterRange))
			return false;
		
		CharacterRange other = (CharacterRange) obj;
		
		return start == other.start && end == other.end;
	}

	@Override
	protected Automaton createAutomaton() {
		State startState = new State();
		State finalState = new State(StateType.FINAL);
		startState.addTransition(new Transition(start, end, finalState));//.addTransitionAction(getPostActions(conditions)));
		return new Automaton(startState, name);
	}
	
	@Override
	public Matcher getMatcher() {
		return (input, i) -> i >= start && i <= end ? 1 : -1;
	}
	
	@Override
	public Matcher getBackwardsMatcher() {
		return getMatcher();
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	@Override
	public boolean isSingleChar() {
		return start == end;
	}
	
	@Override
	public Character asSingleChar() {
		return Character.from(start);
	}

	public CharacterClass not() {
		return UnicodeUtil.reverse(this);
	}

	@Override
	public int compareTo(CharacterRange o) {
		return start == o.start ? end - o.end : start - o.start;
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		Set<CharacterRange> firstSet = new HashSet<>();
		firstSet.add(this);
		return firstSet;
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}

	public static Builder builder(int start, int end) {
		return new Builder(start, end);
	}
	
    @Override
    public SymbolBuilder<? extends Symbol> copyBuilder() {
        return new Builder(this);
    }
	
	@Override
	public String getConstructorCode() {
		return "Range.in(" + start + ", " + end + ")";
	}
	
	public static class Builder extends SymbolBuilder<CharacterRange> {

		private int start;
		private int end;

		public Builder(int start, int end) {
			super(getName(start, end));
			this.start = start;
			this.end = end;
		}
		
		public Builder(CharacterRange range) {
			super(range);
			this.start = range.start;
			this.end = range.end;
		}
		
		@Override
		public CharacterRange build() {
			return new CharacterRange(this);
		}
	}
	
}
