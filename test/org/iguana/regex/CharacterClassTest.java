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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.iguana.grammar.symbol.Constants;
import org.iguana.regex.automaton.Automaton;
import org.iguana.regex.automaton.AutomatonOperations;
import org.iguana.regex.matcher.DFAMatcherFactory;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.junit.Test;

import iguana.utils.input.Input;

public class CharacterClassTest {
	
	MatcherFactory factory = new DFAMatcherFactory();

	@Test
	public void test1() {
		RegularExpression regex = Alt.from(CharacterRange.in('a', 'z'), CharacterRange.in('1', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(6, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		
		Matcher matcher = factory.getMatcher(regex);
		
		assertTrue(matcher.match(Input.fromChar('a')));
		assertTrue(matcher.match(Input.fromChar('f')));
		assertTrue(matcher.match(Input.fromChar('z')));
		assertTrue(matcher.match(Input.fromChar('1')));
		assertTrue(matcher.match(Input.fromChar('5')));
		assertTrue(matcher.match(Input.fromChar('8')));
		
		assertFalse(matcher.match(Input.fromChar('0')));
		assertFalse(matcher.match(Input.fromChar('9')));
		assertFalse(matcher.match(Input.fromChar('*')));
	}
	
	@Test
	public void test2() {
		RegularExpression regex = Alt.from(CharacterRange.in('1', '5'), CharacterRange.in('1', '7'), CharacterRange.in('3', '8'));

		Automaton automaton = regex.getAutomaton();
		assertEquals(8, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(5, automaton.getCountStates());

		Matcher matcher = factory.getMatcher(regex);
		
		assertTrue(matcher.match(Input.fromChar('1')));
		assertTrue(matcher.match(Input.fromChar('2')));
		assertTrue(matcher.match(Input.fromChar('3')));
		assertTrue(matcher.match(Input.fromChar('4')));
		assertTrue(matcher.match(Input.fromChar('5')));
		assertTrue(matcher.match(Input.fromChar('6')));
		assertTrue(matcher.match(Input.fromChar('7')));
		assertTrue(matcher.match(Input.fromChar('8')));
		
		assertFalse(matcher.match(Input.fromChar('0')));
		assertFalse(matcher.match(Input.fromChar('9')));
	}
	
	public void notTest() {
		Alt<CharacterRange> c = Alt.from(CharacterRange.in('0', '9'), CharacterRange.in('a', 'z'));
		Alt<CharacterRange> expected = Alt.from(CharacterRange.in(1, '0' - 1), 
													  CharacterRange.in('9' + 1, 'a' - 1), 
													  CharacterRange.in('z' + 1, Constants.MAX_UTF32_VAL));
		
		assertEquals(expected, Alt.not(c));
	}
	
}
