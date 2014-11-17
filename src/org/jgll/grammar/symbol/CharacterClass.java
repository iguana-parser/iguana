package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.automaton.Automaton;

import static org.jgll.util.generator.GeneratorUtil.*;


/**
 * Character class represents a set of {@link Range} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private final RegexAlt<Range> alt;
	
	public CharacterClass(RegexAlt<Range> alt, String label, Set<Condition> conditions, Object object) {
		super(getName(alt), label, conditions, object);
		this.alt = alt;
	}
	
	public static CharacterClass fromChars(Character...chars) {
		List<Range> list = new ArrayList<>();
		for(Character c : chars) {
			list.add(Range.in(c.getValue(), c.getValue()));
		}
		return new Builder(list).build();
	}
	
	public static CharacterClass from(Range...ranges) {
		return from(Arrays.asList(ranges));
	}
	
	public static CharacterClass from(List<Range> ranges) {
		return new Builder(ranges).build();
	}
	
	private static String getName(RegexAlt<Range> alt) {
		return "[" + listToString(alt.getRegularExpressions()) + "]";
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
	protected Automaton createAutomaton() {
		return alt.getAutomaton();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	public CharacterClass not() {
		List<Range> newRanges = new ArrayList<>();
		
		int i = 0;
		
		Range[] ranges = alt.getRegularExpressions().toArray(new Range[] {});
		Arrays.sort(ranges);
		
		if(ranges[i].getStart() >= 1) {
			newRanges.add(Range.in(1, ranges[i].getStart() - 1));
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
		
		return new Builder(newRanges).addConditions(conditions).build();
	}

	@Override
	public Set<Range> getFirstSet() {
		return alt.getFirstSet();
	}
	
	@Override
	public Set<Range> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	public int size() {
		return alt.size();
	}
	
	public Range get(int index) {
		return alt.get(index);
	}
	
	public static class Builder extends SymbolBuilder<CharacterClass> {

		private RegexAlt<Range> alt;
		
		public Builder(Range...ranges) {
			this(Arrays.asList(ranges));
		}
		
		public Builder(List<Range> ranges) {
			this(RegexAlt.from(ranges));
		}
		
		public Builder(RegexAlt<Range> alt) {
			this.alt = alt;
		}
		
		public Builder(CharacterClass charClass) {
			super(charClass);
			this.alt = charClass.alt;
		}
		
		@Override
		public CharacterClass build() {
			return new CharacterClass(alt, label, conditions, object);
		}
		
	}

	@Override
	public SymbolBuilder<CharacterClass> builder() {
		return new Builder(this);
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new CharacterClass(")
		  .append(alt.getConstructorCode() + ", ")
		  .append("\"" + escape(label) + "\", ")
		  .append("new HashSet<Condition>(), ")
		  .append("null")
		  .append(")");
		return sb.toString();
	}
	
}
