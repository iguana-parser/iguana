package org.jgll.grammar.symbol;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.GrammarRegistry;
import org.jgll.regex.RegexAlt;
import org.jgll.regex.automaton.Automaton;


/**
 * Character class represents a set of {@link CharacterRange} instances.
 * For example, [A-Za-z0-9] represents a character which is
 * either [A-Z], [a-z] or [0-9].
 * 
 * @author Ali Afroozeh
 *
 */
public class CharacterClass extends AbstractRegularExpression {
	
	private static final long serialVersionUID = 1L;
	
	private final RegexAlt<CharacterRange> alt;
	
	private CharacterClass(Builder builder) {
		super(builder);
		this.alt = builder.alt;
	}
	
	public static CharacterClass fromChars(Character...chars) {
		List<CharacterRange> list = new ArrayList<>();
		for(Character c : chars) {
			list.add(CharacterRange.in(c.getValue(), c.getValue()));
		}
		return new Builder(list).build();
	}
	
	public static CharacterClass from(CharacterRange...ranges) {
		return from(Arrays.asList(ranges));
	}
	
	public static CharacterClass from(List<CharacterRange> ranges) {
		return new Builder(ranges).build();
	}
	
	private static String getName(RegexAlt<CharacterRange> alt) {
		return "[" + listToString(alt.getRegularExpressions()) + "]";
	}
	
	@Override
	public int hashCode() {
		return alt.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof CharacterClass))
			return false;
		
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
		List<CharacterRange> newRanges = new ArrayList<>();
		
		int i = 0;
		
		CharacterRange[] ranges = alt.getRegularExpressions().toArray(new CharacterRange[] {});
		Arrays.sort(ranges);
		
		if(ranges[i].getStart() >= 1) {
			newRanges.add(CharacterRange.in(1, ranges[i].getStart() - 1));
		}
		
		for(; i < ranges.length - 1; i++) {
			CharacterRange r1 = ranges[i];
			CharacterRange r2 = ranges[i + 1];
			
			if(r2.getStart() > r1.getEnd() + 1) {
				newRanges.add(CharacterRange.in(r1.getEnd() + 1, r2.getStart() - 1));
			}
		}
		
		if(ranges[i].getEnd() < Constants.MAX_UTF32_VAL) {
			newRanges.add(CharacterRange.in(ranges[i].getEnd() + 1, Constants.MAX_UTF32_VAL));
		}
		
		return new Builder(newRanges).addPreConditions(preConditions).build();
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		return alt.getFirstSet();
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	public int size() {
		return alt.size();
	}
	
	public CharacterRange get(int index) {
		return alt.get(index);
	}
	
	public static Builder builder(CharacterRange...ranges) {
		return new Builder(ranges);
	}
	
	public static Builder builder(List<CharacterRange> ranges) {
		return new Builder(ranges);
	}

    @Override
    public SymbolBuilder<? extends Symbol> copyBuilder() {
        return new Builder(this);
    }

	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new CharacterClass(")
		  .append(alt.getConstructorCode(registry) + ", ")
		  .append("\"" + escape(label) + "\", ")
		  .append("new HashSet<Condition>(), ")
		  .append("null")
		  .append(")");
		return sb.toString();
	}
	
	public static class Builder extends SymbolBuilder<CharacterClass> {

		private RegexAlt<CharacterRange> alt;
		
		public Builder(CharacterRange...ranges) {
			this(Arrays.asList(ranges));
		}
		
		public Builder(List<CharacterRange> ranges) {
			this(RegexAlt.from(ranges));
		}
		
		public Builder(RegexAlt<CharacterRange> alt) {
			super(getName(alt));
			this.alt = alt;
		}
		
		public Builder(CharacterClass charClass) {
			super(charClass);
			this.alt = charClass.alt;
		}
		
		@Override
		public CharacterClass build() {
			return new CharacterClass(this);
		}
		
	}
}
