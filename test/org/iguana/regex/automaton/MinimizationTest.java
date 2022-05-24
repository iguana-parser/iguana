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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinimizationTest {

	@Test
	public void test1() {
		org.iguana.regex.automaton.State q0 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State q1 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State q2 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State q3 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State q4 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State q5 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		org.iguana.regex.automaton.State q6 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		org.iguana.regex.automaton.State q7 = new org.iguana.regex.automaton.State();
		
		q0.addTransition(new org.iguana.regex.automaton.Transition('0', q7));
		q0.addTransition(new org.iguana.regex.automaton.Transition('1', q1));
		
		q1.addTransition(new org.iguana.regex.automaton.Transition('0', q7));
		q1.addTransition(new org.iguana.regex.automaton.Transition('1', q0));

		q2.addTransition(new org.iguana.regex.automaton.Transition('0', q4));
		q2.addTransition(new org.iguana.regex.automaton.Transition('1', q5));
		
		q3.addTransition(new org.iguana.regex.automaton.Transition('0', q4));
		q3.addTransition(new org.iguana.regex.automaton.Transition('1', q5));
		
		q4.addTransition(new org.iguana.regex.automaton.Transition('1', q6));
		
		q5.addTransition(new org.iguana.regex.automaton.Transition('0', q5));
		q5.addTransition(new org.iguana.regex.automaton.Transition('1', q5));
		
		q6.addTransition(new org.iguana.regex.automaton.Transition('0', q6));
		q6.addTransition(new org.iguana.regex.automaton.Transition('1', q5));
		
		q7.addTransition(new org.iguana.regex.automaton.Transition('0', q2));
		q7.addTransition(new org.iguana.regex.automaton.Transition('1', q2));
		
		org.iguana.regex.automaton.Automaton nfa = org.iguana.regex.automaton.Automaton.builder(q0).minimize().build();
		
		assertEquals(getAutomaton1(), nfa);
	}
	
	private org.iguana.regex.automaton.Automaton getAutomaton1() {
		org.iguana.regex.automaton.State state1 = new org.iguana.regex.automaton.State();
		state1.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state1));
		org.iguana.regex.automaton.State state2 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state3 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state4 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state5 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		state5.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state5));
		state5.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state5));
		state4.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state5));
		state3.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state4));
		state3.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state5));
		state2.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state3));
		state2.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state3));
		state1.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state2));
		
		return org.iguana.regex.automaton.Automaton.builder(state1).build();
	}
	
	
	public void test2() {
		org.iguana.regex.automaton.State a = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State b = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State c = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State d = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State e = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		
		a.addTransition(new org.iguana.regex.automaton.Transition('0', b));
		a.addTransition(new org.iguana.regex.automaton.Transition('1', c));
		b.addTransition(new org.iguana.regex.automaton.Transition('0', '1', d));
		c.addTransition(new org.iguana.regex.automaton.Transition('0', '1', d));
		d.addTransition(new org.iguana.regex.automaton.Transition('0', '1', e));
		
		org.iguana.regex.automaton.Automaton minimized = org.iguana.regex.automaton.Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton2(), minimized);
	}
	
	private org.iguana.regex.automaton.Automaton getAutomaton2() {
		org.iguana.regex.automaton.State state1 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state2 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state3 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state4 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		state3.addTransition(new org.iguana.regex.automaton.Transition(48, 49, state4));
		state2.addTransition(new org.iguana.regex.automaton.Transition(48, 49, state3));
		state1.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state2));
		state1.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state2));
		return org.iguana.regex.automaton.Automaton.builder(state1).build();
	}
	
	
	public void test3() {
		org.iguana.regex.automaton.State a = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		org.iguana.regex.automaton.State b = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State c = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		org.iguana.regex.automaton.State d = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State e = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		
		a.addTransition(new org.iguana.regex.automaton.Transition('0', a));
		a.addTransition(new org.iguana.regex.automaton.Transition('1', b));
		b.addTransition(new org.iguana.regex.automaton.Transition('0', c));
		b.addTransition(new org.iguana.regex.automaton.Transition('1', d));
		c.addTransition(new org.iguana.regex.automaton.Transition('0', c));
		c.addTransition(new org.iguana.regex.automaton.Transition('1', e));
		d.addTransition(new org.iguana.regex.automaton.Transition('0', c));
		d.addTransition(new org.iguana.regex.automaton.Transition('1', d));
		e.addTransition(new org.iguana.regex.automaton.Transition('0', e));
		e.addTransition(new org.iguana.regex.automaton.Transition('1', e));
		
		org.iguana.regex.automaton.Automaton minimized = org.iguana.regex.automaton.Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton3(), minimized);
	}
	
	private org.iguana.regex.automaton.Automaton getAutomaton3() {
		org.iguana.regex.automaton.State state1 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		state1.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state1));
		org.iguana.regex.automaton.State state2 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state3 = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		state3.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state3));
		state3.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state3));
		state2.addTransition(new org.iguana.regex.automaton.Transition(48, 48, state3));
		state2.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state2));
		state1.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state2));
		return org.iguana.regex.automaton.Automaton.builder(state1).build();
	}
	
	public void test4() {
		org.iguana.regex.automaton.State a = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State b = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State c = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State d = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State e = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State f = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State g = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State h = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State i = new org.iguana.regex.automaton.State(org.iguana.regex.automaton.StateType.FINAL);
		
		a.addTransition(new org.iguana.regex.automaton.Transition('1', b));
		a.addTransition(new org.iguana.regex.automaton.Transition('2', c));
		a.addTransition(new org.iguana.regex.automaton.Transition('3', d));
		b.addTransition(new org.iguana.regex.automaton.Transition('1', '3', e));
		c.addTransition(new org.iguana.regex.automaton.Transition('1', '3', e));
		d.addTransition(new org.iguana.regex.automaton.Transition('1', '3', e));
		e.addTransition(new org.iguana.regex.automaton.Transition('1', f));
		e.addTransition(new org.iguana.regex.automaton.Transition('2', g));
		e.addTransition(new org.iguana.regex.automaton.Transition('3', h));
		f.addTransition(new org.iguana.regex.automaton.Transition('1', '3', i));
		g.addTransition(new org.iguana.regex.automaton.Transition('1', '3', i));
		h.addTransition(new org.iguana.regex.automaton.Transition('1', i));
		
		org.iguana.regex.automaton.Automaton minimized = org.iguana.regex.automaton.Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton4(), minimized);
	}
	
	private org.iguana.regex.automaton.Automaton getAutomaton4() {
		org.iguana.regex.automaton.State state1 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state2 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state3 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state4 = new org.iguana.regex.automaton.State();
		org.iguana.regex.automaton.State state5 = new org.iguana.regex.automaton.State(StateType.FINAL);
		state4.addTransition(new org.iguana.regex.automaton.Transition(49, 51, state5));
		state3.addTransition(new org.iguana.regex.automaton.Transition(50, 50, state4));
		org.iguana.regex.automaton.State state6 = new State();
		state6.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state5));
		state3.addTransition(new org.iguana.regex.automaton.Transition(51, 51, state6));
		state3.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state4));
		state2.addTransition(new org.iguana.regex.automaton.Transition(49, 51, state3));
		state1.addTransition(new org.iguana.regex.automaton.Transition(50, 50, state2));
		state1.addTransition(new org.iguana.regex.automaton.Transition(49, 49, state2));
		state1.addTransition(new Transition(51, 51, state2));
		return Automaton.builder(state1).build();
	}
	
}
