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
import org.iguana.regex.matcher.DFAMatcher;
import org.iguana.regex.matcher.DFAMatcherFactory;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.junit.Test;

import static iguana.utils.collections.CollectionsUtil.set;
import static org.junit.Assert.assertEquals;


public class AltTest {
	
	MatcherFactory factory = new DFAMatcherFactory();
	
	@Test
	public void test1() {
		org.iguana.regex.Char a = org.iguana.regex.Char.from('a');
		org.iguana.regex.Char b = org.iguana.regex.Char.from('b');

		org.iguana.regex.RegularExpression regex = org.iguana.regex.Alt.from(a, b);
		
		Automaton automaton = regex.getAutomaton();
		assertEquals(5, automaton.getCountStates());
		assertEquals(set(a, b, regex), automaton.getRegularExpressions());

		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		assertEquals(set(a, b, regex), automaton.getRegularExpressions());
		
		Matcher dfa = new DFAMatcher(automaton);
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		org.iguana.regex.Alt<Seq<Char>> alt = org.iguana.regex.Alt.from(org.iguana.regex.Seq.from("for"), org.iguana.regex.Seq.from("forall"));
		Matcher matcher = factory.getMatcher(alt);
		assertEquals(3, matcher.match(Input.fromString("for"), 0));
		assertEquals(6, matcher.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regex = Alt.from(org.iguana.regex.Seq.from("when"), Seq.from("if"));
		Matcher matcher = factory.getMatcher(regex);
		
		assertEquals(4, matcher.match(Input.fromString("when"), 0));
		assertEquals(2, matcher.match(Input.fromString("if"), 0));
	}
	
}
