package org.jgll.grammar.symbol;

import java.util.BitSet;
import java.util.Collection;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.NFA;
import org.jgll.regex.State;
import org.jgll.regex.Transition;


/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Range extends AbstractSymbol implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int start;
	
	private final int end;
	
	private final BitSet testSet;
	
	private final NFA nfa;

	public Range(int start, int end) {
		
		if(end < start) {
			throw new IllegalArgumentException("Start cannot be less than end.");
		}
		
		this.start = start;
		this.end = end;
		this.nfa = createNFA();
		
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
		return  "[" + (char) start + "-" + (char) end + "]";
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
	
	private NFA createNFA() {
		State startState = new State();
		State finalState = new State(true);
		startState.addTransition(new Transition(start, end, finalState));
		return new NFA(startState, finalState);
	}

	@Override
	public NFA toNFA() {
		return nfa;
	}

	@Override
	public boolean isNullable() {
		return false;
	}
}
