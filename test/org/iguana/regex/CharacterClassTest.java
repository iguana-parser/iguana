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

import iguana.utils.input.Input;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcherFactory;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterClassTest {
	
	MatcherFactory factory = new DFAMatcherFactory();

	@Test
	public void test1() {
		org.iguana.regex.RegularExpression regex = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('a', 'z'), org.iguana.regex.CharRange.in('1', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(5, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		
		Matcher matcher = factory.getMatcher(regex);
		
		assertTrue(matcher.match(Input.fromString("a")));
		assertTrue(matcher.match(Input.fromString("f")));
		assertTrue(matcher.match(Input.fromString("z")));
		assertTrue(matcher.match(Input.fromString("1")));
		assertTrue(matcher.match(Input.fromString("5")));
		assertTrue(matcher.match(Input.fromString("8")));
		
		assertFalse(matcher.match(Input.fromString("0")));
		assertFalse(matcher.match(Input.fromString("9")));
		assertFalse(matcher.match(Input.fromString("*")));
	}
	
	@Test
	public void test2() {
		RegularExpression regex = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('1', '5'), org.iguana.regex.CharRange.in('1', '7'), org.iguana.regex.CharRange.in('3', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(7, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(5, automaton.getCountStates());

		Matcher matcher = factory.getMatcher(regex);
		
		assertTrue(matcher.match(Input.fromString("1")));
		assertTrue(matcher.match(Input.fromString("2")));
		assertTrue(matcher.match(Input.fromString("3")));
		assertTrue(matcher.match(Input.fromString("4")));
		assertTrue(matcher.match(Input.fromString("5")));
		assertTrue(matcher.match(Input.fromString("6")));
		assertTrue(matcher.match(Input.fromString("7")));
		assertTrue(matcher.match(Input.fromString("8")));
		
		assertFalse(matcher.match(Input.fromString("0")));
		assertFalse(matcher.match(Input.fromString("9")));
	}

	@Test
	public void notTest() {
		org.iguana.regex.Alt<org.iguana.regex.CharRange> expected = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in(1, '0' - 1),
													  org.iguana.regex.CharRange.in('9' + 1, 'a' - 1),
													  org.iguana.regex.CharRange.in('z' + 1, CharacterRanges.MAX_UTF32_VAL));
		
		assertEquals(expected, Alt.not(org.iguana.regex.CharRange.in('0', '9'), CharRange.in('a', 'z')));
	}
	
}
