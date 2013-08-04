package org.jgll.grammar;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jgll.grammar.condition.Condition;

/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractSymbol implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final List<Range> ranges;
	
	private BitSet testSet;
	
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
		if(c == '\r') {
			return "\\r";
		}
		if(c == '\n') {
			return "\\n";
		}
		return c + "";
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
}
