package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import org.jgll.regex.Automaton;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.State;
import org.jgll.regex.Transition;
import org.jgll.util.CollectionsUtil;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractSymbol implements RegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private RegexAlt<Range> alt;
	
	public CharacterClass(Range...ranges) {
		this(Arrays.asList(ranges));
	}
	
	public CharacterClass(List<Range> ranges) {
		this(new RegexAlt<>(ranges));
	}
	
	public CharacterClass(RegexAlt<Range> ranges) {
		this.alt = ranges;
	}
	
	public static CharacterClass fromChars(Character...chars) {
		List<Range> list = new ArrayList<>();
		for(Character c : chars) {
			list.add(Range.in(c.getValue(), c.getValue()));
		}
		return new CharacterClass(list);
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	@Override
	public int hashCode() {
		return alt.hashCode();
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

		return alt.equals(other.alt);
	}

	@Override
	public String getName() {
		return "[" + CollectionsUtil.listToString(alt.getRegularExpressions(), " ") + "]";
	}
		
	@Override
	public BitSet asBitSet() {
		return alt.asBitSet();
	}
	
	private Automaton createNFA() {
		State startState = new State();
		State finalState = new State(true);
		
		for(Range range : alt) {
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
		return alt.copy();
	}
	
	public CharacterClass not() {
		List<Range> newRanges = new ArrayList<>();
		
		int i = 0;
		
		Range[] ranges = alt.getRegularExpressions().toArray(new Range[] {});
		Arrays.sort(ranges);
		
		if(ranges[i].getStart() > 0) {
			newRanges.add(Range.in(0, ranges[i].getStart() - 1));
		}
		
		for(; i < ranges.length - 1; i++) {
			Range r1 = ranges[i];
			Range r2 = ranges[i + 1];
			
			if(r2.getStart() > r1.getEnd() + 1) {
				newRanges.add(Range.in(r1.getEnd() + 1, r2.getStart() - 1));
			}
		}
		
		if(ranges[i].getEnd() < Constants.MAX_UTF32_VAL) {
			newRanges.add(Range.in(ranges[i].getEnd() + 1, Constants.MAX_UTF32_VAL));
		}
		
		return new CharacterClass(newRanges);
	}
}
