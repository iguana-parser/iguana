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

import static org.junit.Assert.*;

public class SeqTest {
	
	MatcherFactory factory = new DFAMatcherFactory();
	
	// ab
	private Seq<Char> seq1 = Seq.builder(Char.from('a'), Char.from('b')).build();
	
	// [a-z][0-9]
	private Seq<CharRange> seq2 = Seq.builder(CharRange.in('a', 'z'), CharRange.in('0', '9')).build();
	
	// [a-z][b-m]
	private Seq<CharRange> seq3 = Seq.builder(CharRange.in('a', 'z'), CharRange.in('b', 'm')).build();

	@Test
	public void testAutomaton1() {
		Automaton automaton = seq1.getAutomaton();
		assertEquals(3, automaton.getCountStates());
	}

    @Test
	public void testDFAMatcher1() {
		Matcher matcher = factory.getMatcher(seq1);
		assertTrue(matcher.match(Input.fromString("ab")));
		assertFalse(matcher.match(Input.fromString("ac")));
		assertFalse(matcher.match(Input.fromString("da")));
	}
	
	@Test
	public void testAutomaton2() {
		Automaton automaton = seq2.getAutomaton();
		assertEquals(3, automaton.getCountStates());
	}
	
	@Test
	public void testDFAMatcher2() {
		Matcher matcher = factory.getMatcher(seq2);
		
		assertTrue(matcher.match(Input.fromString("a0")));
		assertTrue(matcher.match(Input.fromString("a5")));
		assertTrue(matcher.match(Input.fromString("a9")));
		assertTrue(matcher.match(Input.fromString("c7")));
		assertTrue(matcher.match(Input.fromString("z0")));
		assertTrue(matcher.match(Input.fromString("z9")));
		
		assertFalse(matcher.match(Input.fromString("ac")));
		assertFalse(matcher.match(Input.fromString("da")));
	}
	
	@Test
	public void testJavaRegexMatcher2() {
		Matcher dfa = factory.getMatcher(seq2);
		
		assertTrue(dfa.match(Input.fromString("a0")));
		assertTrue(dfa.match(Input.fromString("a5")));
		assertTrue(dfa.match(Input.fromString("a9")));
		assertTrue(dfa.match(Input.fromString("c7")));
		assertTrue(dfa.match(Input.fromString("z0")));
		assertTrue(dfa.match(Input.fromString("z9")));
		
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}

	
	/**
	 * Two character classes with overlapping ranges
	 */
	@Test
	public void testAutomaon3() {
		Automaton automaton = seq3.getAutomaton();
		assertEquals(3, automaton.getCountStates());		
	}
	
	@Test
	public void testDFAMatcher3() {
		Matcher matcher = factory.getMatcher(seq3);
		assertTrue(matcher.match(Input.fromString("dm")));
	}
	
}
