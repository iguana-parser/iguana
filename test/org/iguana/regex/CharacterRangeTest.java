/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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

package org.iguana.regex;

import static org.junit.Assert.*;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.util.Input;
import org.junit.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;

import static org.iguana.util.CharacterRanges.*;

public class CharacterRangeTest {
	
	@Test
	public void overlappingTest1() {
		CharacterRange r1 = CharacterRange.in('a', 'f');
		CharacterRange r2 = CharacterRange.in('d', 'k');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}
	
	@Test
	public void overlappingTest2() {
		CharacterRange r1 = CharacterRange.in('a', 'z');
		CharacterRange r2 = CharacterRange.in('s', 'u');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}

	@Test
	public void overlappingToNonOverlapping1() {
		// 1-5 3-7
		CharacterRange r1 = CharacterRange.in(1, 5);
		CharacterRange r2 = CharacterRange.in(3, 7);
		Multimap<CharacterRange, CharacterRange> nonOverlapping = toNonOverlapping(r1, r2);
		
		// 1-2 3-5 6-7
		CharacterRange t1 = CharacterRange.in(1, 2);
		CharacterRange t2 = CharacterRange.in(3, 5);
		CharacterRange t3 = CharacterRange.in(6, 7);
		ImmutableListMultimap<Object, Object> expected = 
				ImmutableListMultimap.builder().put(r1, t1).put(r1, t2)
			                                   .put(r2, t2).put(r2, t3).build();
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping2() {
		// 1-7 5-13 6-12 17-21
		CharacterRange r2 = CharacterRange.in(5, 13);
		CharacterRange r4 = CharacterRange.in(17, 21);
		CharacterRange r1 = CharacterRange.in(1, 7);
		CharacterRange r3 = CharacterRange.in(6, 12);
		Multimap<CharacterRange, CharacterRange> nonOverlapping = toNonOverlapping(r1, r2, r3, r4);
		
		// 1-4 5-5 6-7 8-12 13-13 17-21
		CharacterRange t1 = CharacterRange.in(1, 4);
		CharacterRange t2 = CharacterRange.in(5, 5);
		CharacterRange t3 = CharacterRange.in(6, 7);
		CharacterRange t4 = CharacterRange.in(8, 12);
		CharacterRange t5 = CharacterRange.in(13, 13);
		CharacterRange t6 = CharacterRange.in(17, 21);
		
		ImmutableListMultimap<Object, Object> expected = 
				ImmutableListMultimap.builder().put(r1, t1).put(r1, t2).put(r1, t3)
				                               .put(r2, t2).put(r2, t3).put(r2, t4).put(r2, t5)
				                               .put(r3, t3).put(r3, t4)
				                               .put(r4, t6).build();
		
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping3() {
		// 1-7 3-5 4-4
		CharacterRange r1 = CharacterRange.in(1, 7);
		CharacterRange r2 = CharacterRange.in(3, 5);
		CharacterRange r3 = CharacterRange.in(4, 4);
		Multimap<CharacterRange, CharacterRange> nonOverlapping = toNonOverlapping(r1, r2, r3);
		
		// 1-2 3-3 4-4 5-5 6-7
		CharacterRange t1 = CharacterRange.in(1, 2);
		CharacterRange t2 = CharacterRange.in(3, 3);
		CharacterRange t3 = CharacterRange.in(4, 4);
		CharacterRange t4 = CharacterRange.in(5, 5);
		CharacterRange t5 = CharacterRange.in(6, 7);
		
		ImmutableListMultimap<Object, Object> expected = 
				ImmutableListMultimap.builder().put(r1, t1).put(r1, t2).put(r1, t3).put(r1, t4).put(r1, t5)
											   .put(r2, t2).put(r2, t3).put(r2, t4)
											   .put(r3, t3).build();
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping4() {
		// 11-12 1-3 5-7
		CharacterRange r1 = CharacterRange.in(11, 12);
		CharacterRange r2 = CharacterRange.in(1, 3);
		CharacterRange r3 = CharacterRange.in(5, 7);
		Multimap<CharacterRange, CharacterRange> nonOverlapping = toNonOverlapping(r1, r2, r3);
		
		// 1-3 5-7 11-12
		CharacterRange t1 = CharacterRange.in(1, 3);
		CharacterRange t2 = CharacterRange.in(5, 7); 
		CharacterRange t3 = CharacterRange.in(11, 12);
		
		ImmutableListMultimap<Object, Object> expected = 
				ImmutableListMultimap.builder().put(r1, t3)
				                               .put(r2, t1)
				                               .put(r3, t2).build();
		
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping5() {
		// 7-9 4-11 4-10 1-12 3-6 1-2
		CharacterRange r1 = CharacterRange.in(7, 9);
		CharacterRange r2 = CharacterRange.in(4, 11);
		CharacterRange r3 = CharacterRange.in(4, 10);
		CharacterRange r4 = CharacterRange.in(1, 12);
		CharacterRange r5 = CharacterRange.in(3, 6);
		CharacterRange r6 = CharacterRange.in(1, 2);
		Multimap<CharacterRange, CharacterRange> nonOverlapping = toNonOverlapping(r1, r2, r3, r4, r5, r6);
		
		// 1-2 3-3 4-6 7-9 10-10 11-11 12-12
		CharacterRange t1 = CharacterRange.in(1, 2);
		CharacterRange t2 = CharacterRange.in(3, 3);
		CharacterRange t3 = CharacterRange.in(4, 6);
		CharacterRange t4 = CharacterRange.in(7, 9);
		CharacterRange t5 = CharacterRange.in(10, 10);
		CharacterRange t6 = CharacterRange.in(11, 11);
		CharacterRange t7 = CharacterRange.in(12, 12);
		
		ImmutableListMultimap<Object, Object> expected = 
				ImmutableListMultimap.builder().put(r1, t4)
				 					           .put(r2, t3).put(r2, t4).put(r2, t5).put(r2, t6)
				 					           .put(r3, t3).put(r3, t4).put(r3, t5)
				 					           .put(r4, t1).put(r4, t2).put(r4, t3).put(r4, t4).put(r4, t5).put(r4, t6).put(r4, t7)
				 					           .put(r5, t2).put(r5, t3)
				 					           .put(r6, t1).build();
		
		assertEquals(expected, nonOverlapping);
	}

	
	@Test
	public void test() {
		CharacterRange range = CharacterRange.in('0', '9');
		Automaton automaton = range.getAutomaton();
		assertEquals(2, automaton.getCountStates());
		Matcher dfa = MatcherFactory.getMatcher(range);
		
		assertEquals(1, dfa.match(Input.fromString("0"), 0));
		assertEquals(1, dfa.match(Input.fromString("3"), 0));
		assertEquals(1, dfa.match(Input.fromString("9"), 0));
	}	

}
