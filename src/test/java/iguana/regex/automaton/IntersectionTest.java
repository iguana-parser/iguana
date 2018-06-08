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

import iguana.regex.RegularExpression;
import iguana.regex.RegularExpressionExamples;
import iguana.regex.matcher.DFAMatcher;
import iguana.utils.input.Input;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntersectionTest {

	@Test
	public void test1() {
		
		State x0 = new State();
		State x1 = new State(StateType.FINAL);
		
		x0.addTransition(new Transition('0', x0));
		x0.addTransition(new Transition('1', x1));
		x1.addTransition(new Transition('0', x1));
		x1.addTransition(new Transition('1', x0));
		
		// Matches an odd number of 1's.
		Automaton a1 = Automaton.builder(x0).build();
		
		State y0 = new State(StateType.FINAL);
		State y1 = new State();
		
		y0.addTransition(new Transition('0', y1));
		y0.addTransition(new Transition('1', y1));
		y1.addTransition(new Transition('0', y0));
		y1.addTransition(new Transition('1', y0));
		
		// Matches an string of even length.
		Automaton a2 = Automaton.builder(y0).build();
		
		Automaton intersect = AutomatonOperations.intersect(a1, a2);
		DFAMatcher matcher = new DFAMatcher(intersect);
		
		assertFalse(intersect.isLanguageEmpty());
		assertTrue(matcher.match(Input.fromString("111001110001")));
		assertFalse(matcher.match(Input.fromString("111001010001")));
		assertFalse(matcher.match(Input.fromString("1110011100010")));
	}
	
	@Test
	public void test2() {
		RegularExpression f = RegularExpressionExamples.getFloat();
		RegularExpression id = RegularExpressionExamples.getId();
		
		Automaton intersect = AutomatonOperations.intersect(f.getAutomaton(), id.getAutomaton());
		
		// Should not overlap, therefore the intersection should be empty.
		assertTrue(intersect.isLanguageEmpty());
	}
		
}
