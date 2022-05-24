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

import iguana.utils.input.Input;
import org.iguana.regex.Char;
import org.iguana.regex.matcher.DFAMatcher;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ReverseAutomatonTest {
	
	@Test
	public void test1() {
		org.iguana.regex.Seq<org.iguana.regex.Char> r = org.iguana.regex.Seq.from("test");
		org.iguana.regex.automaton.Automaton a = org.iguana.regex.automaton.AutomatonOperations.reverse(r.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("tset")));
	}
	
	@Test
	public void test2() {
		org.iguana.regex.RegularExpression r = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('a', 'z'), org.iguana.regex.CharRange.in('A', 'Z'), org.iguana.regex.CharRange.in('0', '9'), Char.from('_'));
		Automaton a = AutomatonOperations.reverse(r.getAutomaton());
		DFAMatcher matcher = new DFAMatcher(a);
		assertTrue(matcher.match(Input.fromString("a")));
		assertTrue(matcher.match(Input.fromString("m")));
		assertTrue(matcher.match(Input.fromString("z")));
		assertTrue(matcher.match(Input.fromString("A")));
		assertTrue(matcher.match(Input.fromString("M")));
		assertTrue(matcher.match(Input.fromString("Z")));
		assertTrue(matcher.match(Input.fromString("0")));
		assertTrue(matcher.match(Input.fromString("9")));
		assertTrue(matcher.match(Input.fromString("3")));
		assertTrue(matcher.match(Input.fromString("_")));
	}

}
