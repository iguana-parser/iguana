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

package iguana.regex.automaton;

import iguana.utils.input.Input;
import iguana.regex.Alt;
import iguana.regex.RegularExpression;
import iguana.regex.Character;
import iguana.regex.RegularExpressionExamples;
import iguana.regex.Sequence;
import iguana.regex.matcher.DFAMatcher;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class DiffernceTest {
	
	private RegularExpression id = RegularExpressionExamples.getId();
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), k1.getAutomaton());
		
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("i")));
		assertTrue(matcher.match(Input.fromString("iif")));
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("if:")));
		assertFalse(matcher.match(Input.fromString("first:")));
	}
	
	@Test
	public void test2() {
		Automaton union = AutomatonOperations.union(k1.getAutomaton(), k2.getAutomaton());
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), union);

		DFAMatcher matcher = new DFAMatcher(a);
		System.out.println(matcher.match(Input.fromString("f"), 0));
		
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("when")));
	}
	
	@Test
	public void test3() {
		Alt<Sequence<Character>> alt = Alt.from(k1, k2, k3);
		Automaton a = AutomatonOperations.difference(id.getAutomaton(), alt.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("first")));
		assertFalse(matcher.match(Input.fromString("if")));
		assertFalse(matcher.match(Input.fromString("when")));
		assertFalse(matcher.match(Input.fromString("new")));
	}

}
