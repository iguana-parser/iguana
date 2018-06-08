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
import iguana.regex.automaton.AutomatonOperations;
import iguana.regex.matcher.DFAMatcher;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.input.Input;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.*;

public class OptTest {
	
	MatcherFactory factory = new DFAMatcherFactory();
	
	@Test
	public void test1() {
		RegularExpression a = Char.from('a');
		RegularExpression regex = Opt.from(Char.from('a'));
		Automaton automaton = regex.getAutomaton();
		assertEquals(2, automaton.getCountStates());
		assertEquals(set(regex, a), automaton.getRegularExpressions());

		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(2, automaton.getCountStates());
		assertEquals(set(a, regex), automaton.getRegularExpressions());

		Matcher matcher = new DFAMatcher(automaton);
		assertTrue(matcher.match(Input.fromString("a")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}
	
	@Test
	public void test2() {
		RegularExpression regex = Opt.from(Seq.from("integer"));
		Automaton automaton = regex.getAutomaton();
		assertEquals(8, automaton.getCountStates());

		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(8, automaton.getCountStates());

		Matcher matcher = factory.getMatcher(regex);
		assertTrue(matcher.match(Input.fromString("integer")));
		assertFalse(matcher.match(Input.fromString("int")));
		assertTrue(matcher.matchPrefix(Input.fromString("int")));
		assertEquals(0, matcher.match(Input.fromString(""), 0));
	}

}
