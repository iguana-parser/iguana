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

package iguana.regex;

import iguana.regex.automaton.Automaton;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.input.Input;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static iguana.regex.CharacterRanges.toNonOverlapping;
import static iguana.utils.collections.CollectionsUtil.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharRangeTest {
	
	MatcherFactory factory = new DFAMatcherFactory();
	
	@Test
	public void overlappingTest1() {
		CharRange r1 = CharRange.in('a', 'f');
		CharRange r2 = CharRange.in('d', 'k');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}
	
	@Test
	public void overlappingTest2() {
		CharRange r1 = CharRange.in('a', 'z');
		CharRange r2 = CharRange.in('s', 'u');
		assertTrue(r1.overlaps(r2));
		assertTrue(r2.overlaps(r1));
	}

	@Test
	public void overlappingToNonOverlapping1() {
		// 1-5 3-7
		CharRange r1 = CharRange.in(1, 5);
		CharRange r2 = CharRange.in(3, 7);
		Map<CharRange, List<CharRange>> nonOverlapping = toNonOverlapping(r1, r2);
		
		// 1-2 3-5 6-7
		CharRange t1 = CharRange.in(1, 2);
		CharRange t2 = CharRange.in(3, 5);
		CharRange t3 = CharRange.in(6, 7);
		
		Map<CharRange, List<CharRange>> expected = map(list(tuple(r1, list(t1, t2)),
				                                                      tuple(r2, list(t2, t3)))); 
				
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping2() {
		// 1-7 5-13 6-12 17-21
		CharRange r2 = CharRange.in(5, 13);
		CharRange r4 = CharRange.in(17, 21);
		CharRange r1 = CharRange.in(1, 7);
		CharRange r3 = CharRange.in(6, 12);
		Map<CharRange, List<CharRange>> nonOverlapping = toNonOverlapping(r1, r2, r3, r4);
		
		// 1-4 5-5 6-7 8-12 13-13 17-21
		CharRange t1 = CharRange.in(1, 4);
		CharRange t2 = CharRange.in(5, 5);
		CharRange t3 = CharRange.in(6, 7);
		CharRange t4 = CharRange.in(8, 12);
		CharRange t5 = CharRange.in(13, 13);
		CharRange t6 = CharRange.in(17, 21);
		
		Map<CharRange, List<CharRange>> expected = map(list(tuple(r1, list(t1, t2, t3)),
				                                                      tuple(r2, list(t2, t3, t4, t5)),
				                                                      tuple(r3, list(t3, t4)),
				                                                      tuple(r4, list(t6))));
		
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping3() {
		// 1-7 3-5 4-4
		CharRange r1 = CharRange.in(1, 7);
		CharRange r2 = CharRange.in(3, 5);
		CharRange r3 = CharRange.in(4, 4);
		Map<CharRange, List<CharRange>> nonOverlapping = toNonOverlapping(r1, r2, r3);
		
		// 1-2 3-3 4-4 5-5 6-7
		CharRange t1 = CharRange.in(1, 2);
		CharRange t2 = CharRange.in(3, 3);
		CharRange t3 = CharRange.in(4, 4);
		CharRange t4 = CharRange.in(5, 5);
		CharRange t5 = CharRange.in(6, 7);
		
		Map<CharRange, List<CharRange>> expected = map(list(tuple(r1, list(t1, t2, t3, t4, t5)),
				                                                      tuple(r2, list(t2, t3, t4)),
				                                                      tuple(r3, list(t3))));
		
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping4() {
		// 11-12 1-3 5-7
		CharRange r1 = CharRange.in(11, 12);
		CharRange r2 = CharRange.in(1, 3);
		CharRange r3 = CharRange.in(5, 7);
		Map<CharRange, List<CharRange>> nonOverlapping = toNonOverlapping(r1, r2, r3);
		
		// 1-3 5-7 11-12
		CharRange t1 = CharRange.in(1, 3);
		CharRange t2 = CharRange.in(5, 7);
		CharRange t3 = CharRange.in(11, 12);
		
		Map<CharRange, List<CharRange>> expected = map(list(tuple(r1, list(t3)),
				                                                      tuple(r2, list(t1)),
				                                                      tuple(r3, list(t2))));
		
		assertEquals(expected, nonOverlapping);
	}
	
	@Test
	public void overlappingToNonOverlapping5() {
		// 7-9 4-11 4-10 1-12 3-6 1-2
		CharRange r1 = CharRange.in(7, 9);
		CharRange r2 = CharRange.in(4, 11);
		CharRange r3 = CharRange.in(4, 10);
		CharRange r4 = CharRange.in(1, 12);
		CharRange r5 = CharRange.in(3, 6);
		CharRange r6 = CharRange.in(1, 2);
		Map<CharRange, List<CharRange>> nonOverlapping = toNonOverlapping(r1, r2, r3, r4, r5, r6);
		
		// 1-2 3-3 4-6 7-9 10-10 11-11 12-12
		CharRange t1 = CharRange.in(1, 2);
		CharRange t2 = CharRange.in(3, 3);
		CharRange t3 = CharRange.in(4, 6);
		CharRange t4 = CharRange.in(7, 9);
		CharRange t5 = CharRange.in(10, 10);
		CharRange t6 = CharRange.in(11, 11);
		CharRange t7 = CharRange.in(12, 12);
		
		Map<CharRange, List<CharRange>> expected = map(list(tuple(r1, list(t4)),
				                                                      tuple(r2, list(t3, t4, t5, t6)),
				                                                      tuple(r3, list(t3, t4, t5)),
				                                                      tuple(r4, list(t1, t2, t3, t4, t5, t6, t7)),
				                                                      tuple(r5, list(t2, t3)),
				                                                      tuple(r6, list(t1))));
		
		assertEquals(expected, nonOverlapping);
	}

	
	@Test
	public void test() {
		CharRange range = CharRange.in('0', '9');
		Automaton automaton = range.getAutomaton();
		assertEquals(2, automaton.getCountStates());
		Matcher dfa = factory.getMatcher(range);
		
		assertEquals(1, dfa.match(Input.fromString("0"), 0));
		assertEquals(1, dfa.match(Input.fromString("3"), 0));
		assertEquals(1, dfa.match(Input.fromString("9"), 0));
	}	

}
