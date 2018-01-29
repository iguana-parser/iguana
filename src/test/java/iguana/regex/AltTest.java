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
import iguana.regex.automaton.AutomatonOperations;
import iguana.regex.matcher.DFAMatcher;
import iguana.regex.matcher.DFAMatcherFactory;
import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import org.junit.Test;


import static org.junit.Assert.assertEquals;


public class AltTest {
	
	MatcherFactory factory = new DFAMatcherFactory();
	
	@Test
	public void test1() {
		Char a = Char.from('a');
		Char b = Char.from('b');
		
		RegularExpression regex = Alt.from(a, b);
		
		Automaton automaton = regex.getAutomaton();
		assertEquals(6, automaton.getCountStates());
		
		automaton = AutomatonOperations.makeDeterministic(automaton);
		assertEquals(3, automaton.getCountStates());
		
		Matcher dfa = new DFAMatcher(automaton);
		assertEquals(1, dfa.match(Input.fromString("a"), 0));
		assertEquals(1, dfa.match(Input.fromString("b"), 0));
	}
	
	@Test
	public void test2() {
		Alt<Seq<Char>> alt = Alt.from(Seq.from("for"), Seq.from("forall"));
		Matcher matcher = factory.getMatcher(alt);
		assertEquals(3, matcher.match(Input.fromString("for"), 0));
		assertEquals(6, matcher.match(Input.fromString("forall"), 0));
	}
	
	@Test
	public void test3() {
		RegularExpression regex = Alt.from(Seq.from("when"), Seq.from("if"));
		Matcher matcher = factory.getMatcher(regex);
		
		assertEquals(4, matcher.match(Input.fromString("when"), 0));
		assertEquals(2, matcher.match(Input.fromString("if"), 0));
	}
	
}
