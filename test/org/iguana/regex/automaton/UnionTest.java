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

package org.iguana.regex.automaton;

import static org.iguana.regex.automaton.AutomatonOperations.union;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.iguana.regex.Character;
import org.iguana.regex.Sequence;
import org.iguana.regex.matcher.DFAMatcher;
import org.junit.Test;

import iguana.utils.input.Input;

public class UnionTest {
	
	private Sequence<Character> k1 = Sequence.from("if");
	private Sequence<Character> k2 = Sequence.from("when");
	private Sequence<Character> k3 = Sequence.from("new");

	@Test
	public void test1() {
		Automaton a = union(k1.getAutomaton(), k2.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);

		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertFalse(matcher.match(Input.fromString("i")));
		assertFalse(matcher.match(Input.fromString("w")));
		assertFalse(matcher.match(Input.fromString("wh")));
		assertFalse(matcher.match(Input.fromString("whe")));
		assertFalse(matcher.match(Input.fromString("whenever")));
		assertFalse(matcher.match(Input.fromString("else")));
	}
	
	@Test
	public void test3() {
		Automaton a = union(k1.getAutomaton(), union(k2.getAutomaton(), k3.getAutomaton()));
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("if")));
		assertTrue(matcher.match(Input.fromString("when")));
		assertTrue(matcher.match(Input.fromString("new")));
		assertFalse(matcher.match(Input.fromString("i")));
		assertFalse(matcher.match(Input.fromString("w")));
		assertFalse(matcher.match(Input.fromString("n")));
		assertFalse(matcher.match(Input.fromString("ne")));
		assertFalse(matcher.match(Input.fromString("news")));
		assertFalse(matcher.match(Input.fromString("else")));
	}

}
