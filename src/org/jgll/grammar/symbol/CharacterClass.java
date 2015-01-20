package org.jgll.grammar.symbol;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jgll.regex.Alt;
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
	
	private final List<CharacterRange> ranges;
	
	private CharacterClass(Builder builder) {
		super(builder);
		this.ranges = builder.ranges;
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
	
	private static String getName(List<CharacterRange> ranges) {
		return "[" + listToString(ranges) + "]";
	}
	
	@Override
	public int hashCode() {
		return ranges.hashCode();
	}
	
	@Override
	public boolean isSingleChar() {
		return ranges.size() == 1 && ranges.get(0).isSingleChar();
	}
	
	@Override
	public Character asSingleChar() {
		return ranges.get(0).asSingleChar();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (!(obj instanceof CharacterClass))
			return false;
		
		CharacterClass other = (CharacterClass) obj;

		return ranges.equals(other.ranges);
	}

	@Override
	protected Automaton createAutomaton() {
		return Alt.from(ranges).getAutomaton();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	public CharacterClass not() {
		List<CharacterRange> newRanges = new ArrayList<>();
		
		int i = 0;
		
		Collections.sort(ranges);
		
		if(ranges.get(i).getStart() >= 1) {
			newRanges.add(CharacterRange.in(1, ranges.get(i).getStart() - 1));
		}
		
		for(; i < ranges.size() - 1; i++) {
			CharacterRange r1 = ranges.get(i);
			CharacterRange r2 = ranges.get(i + i);
			
			if(r2.getStart() > r1.getEnd() + 1) {
				newRanges.add(CharacterRange.in(r1.getEnd() + 1, r2.getStart() - 1));
			}
		}
		
		if(ranges.get(i).getEnd() < Constants.MAX_UTF32_VAL) {
			newRanges.add(CharacterRange.in(ranges.get(i).getEnd() + 1, Constants.MAX_UTF32_VAL));
		}
		
		return new Builder(newRanges).build();
	}

	@Override
	public Set<CharacterRange> getFirstSet() {
		return ranges.stream().flatMap(r -> r.getFirstSet().stream()).collect(Collectors.toSet());
	}
	
	@Override
	public Set<CharacterRange> getNotFollowSet() {
		return Collections.emptySet();
	}
	
	public int size() {
		return ranges.size();
	}
	
	public CharacterRange get(int index) {
		return ranges.get(index);
	}
	
	public static Builder builder(CharacterRange...ranges) {
		return builder(Arrays.asList(ranges));
	}
	
	public static Builder builder(List<CharacterRange> ranges) {
		return new Builder(ranges);
	}

    @Override
    public SymbolBuilder<? extends Symbol> copyBuilder() {
        return new Builder(this);
    }

	@Override
	public String getConstructorCode() {
		return CharacterClass.class.getName() + ".builder(" + getConstructorCode(ranges) + ").build()";
	}

	@Override
	public Pattern getPattern() {
		throw new UnsupportedOperationException();
	}
	
	public static class Builder extends SymbolBuilder<CharacterClass> {

		private List<CharacterRange> ranges;
		
		public Builder() {
			ranges = new ArrayList<>();
		}
		
		public Builder(List<CharacterRange> ranges) {
			this.ranges = ranges;
		}
		
		public Builder(CharacterClass charClass) {
			this(charClass.ranges);
		}
		
		@Override
		public CharacterClass build() {
			this.name = getName(ranges);
			return new CharacterClass(this);
		}
	}

}
