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

import iguana.utils.input.Input;
import iguana.regex.automaton.Automaton;
import iguana.regex.matcher.DFAMatcher;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterTest {
	
	MatcherFactory factory = new DFAMatcherFactory();

	Character c = Character.from('a');
	
	@Test
	public void testAutomaton() {
		Automaton a = c.getAutomaton();
		assertArrayEquals(new CharacterRange[] {CharacterRange.in('a', 'a')}, a.getAlphabet());
		assertEquals(2, a.getCountStates());
	}



    @Test
	public void testMatcher() {
		Matcher matcher = factory.getMatcher(c);
		Input input = Input.fromString("a");
		assertTrue(matcher.match(input));
		assertEquals(1, matcher.match(input, 0));
	}
	
	@Test
	public void testDFAMatcher() {
		Automaton automaton = c.getAutomaton();
		DFAMatcher matcher = new DFAMatcher(automaton);
		Input input = Input.fromString("a");
		assertEquals(1, matcher.match(input, 0));
	}

}
