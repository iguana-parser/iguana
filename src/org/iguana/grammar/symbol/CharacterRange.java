/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.grammar.symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.iguana.parser.HashFunctions;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.State;
import org.iguana.regex.automaton.StateType;
import org.iguana.regex.automaton.Transition;
import org.iguana.traversal.ISymbolVisitor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;


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
		
		if (builder.end < builder.start) 
			throw new IllegalArgumentException("Start cannot be less than end.");
		
		this.start = builder.start;
		this.end = builder.end;
	}

	public static String getName(int start, int end) {
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
	
	public boolean contains(int c) {
		return start <= c && c <= end;
	}
	
	public boolean contains(CharacterRange other) {
		return start <= other.start && end >= other.end;
	}
	
	public boolean overlaps(CharacterRange other) {
		return !(end < other.start || other.end < start);
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
		startState.addTransition(new Transition(start, end, finalState));
		return Automaton.builder(startState).build();
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
	
	@Override
	public boolean isTerminal() {
		return true;
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
	
	public static Multimap<CharacterRange, CharacterRange> toNonOverlapping(CharacterRange...ranges) {
		return toNonOverlapping(Arrays.asList(ranges));
	}
	
	public static Multimap<CharacterRange, CharacterRange> toNonOverlapping(List<CharacterRange> ranges) {
		
		if (ranges.size() == 0)
			return ImmutableListMultimap.of();
		
		if (ranges.size() == 1) 
			return ImmutableListMultimap.of(ranges.get(0), ranges.get(0));
		
		Collections.sort(ranges);

		Multimap<CharacterRange, CharacterRange> result = ArrayListMultimap.create();
		
		Set<CharacterRange> overlapping = new HashSet<>();
		
		for (int i = 0; i < ranges.size(); i++) {
			
			CharacterRange current = ranges.get(i);
			overlapping.add(current);

			if (i + 1 < ranges.size()) {
				CharacterRange next = ranges.get(i + 1);
				if (!overlaps(overlapping, next)) {
					result.putAll(convertOverlapping(overlapping));
					overlapping.clear();
				}
			}
		}
		
		result.putAll(convertOverlapping(overlapping));
		
		return result;
	}
	
	private static boolean overlaps(Set<CharacterRange> set, CharacterRange r) {
		for (CharacterRange c : set) {
			if (c.overlaps(r)) {
				return true;
			}
		}
		return false;
	}
	
	private static Multimap<CharacterRange, CharacterRange> convertOverlapping(Set<CharacterRange> ranges) {
		
		if (ranges.isEmpty())
			return ImmutableListMultimap.of();
		
		Set<Integer> set = new HashSet<>();
		for (CharacterRange r : ranges) {
			set.add(r.start - 1);
			set.add(r.end);
		}
		List<Integer> l = new ArrayList<>(set);
		Collections.sort(l);
		
		List<CharacterRange> result = new ArrayList<>();
		
		int start = l.get(0) + 1;
		for (int i = 1; i < l.size(); i++) {
			result.add(CharacterRange.in(start, l.get(i)));
			start = l.get(i) + 1;
		}
		
		Multimap<CharacterRange, CharacterRange> rangesMap = ArrayListMultimap.create();
		for (CharacterRange r1 : ranges) {
			for (CharacterRange r2 : result) {
				if (r1.contains(r2))
					rangesMap.put(r1, r2);
			}
		}
		
		return rangesMap;
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
		return CharacterRange.class.getSimpleName() + ".builder(" + start + ", " + end + ")" + super.getConstructorCode() + ".build()";
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

	@Override
	public String getPattern() {
		return "[" + getName() + "]";
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
