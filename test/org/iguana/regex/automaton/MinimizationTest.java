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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MinimizationTest {
	
	@Test
	public void test1() {
		State q0 = new State();
		State q1 = new State();
		State q2 = new State();
		State q3 = new State();
		State q4 = new State();
		State q5 = new State(StateType.FINAL);
		State q6 = new State(StateType.FINAL);
		State q7 = new State();
		
		q0.addTransition(new Transition('0', q7));
		q0.addTransition(new Transition('1', q1));
		
		q1.addTransition(new Transition('0', q7));
		q1.addTransition(new Transition('1', q0));

		q2.addTransition(new Transition('0', q4));
		q2.addTransition(new Transition('1', q5));
		
		q3.addTransition(new Transition('0', q4));
		q3.addTransition(new Transition('1', q5));
		
		q4.addTransition(new Transition('1', q6));
		
		q5.addTransition(new Transition('0', q5));
		q5.addTransition(new Transition('1', q5));
		
		q6.addTransition(new Transition('0', q6));
		q6.addTransition(new Transition('1', q5));
		
		q7.addTransition(new Transition('0', q2));
		q7.addTransition(new Transition('1', q2));
		
		Automaton nfa = Automaton.builder(q0).minimize().build();
		
		assertEquals(getAutomaton1(), nfa);
	}
	
	private Automaton getAutomaton1() {
		State state1 = new State();
		state1.addTransition(new Transition(49, 49, state1));
		State state2 = new State();
		State state3 = new State();
		State state4 = new State();
		State state5 = new State(StateType.FINAL);
		state5.addTransition(new Transition(49, 49, state5));
		state5.addTransition(new Transition(48, 48, state5));
		state4.addTransition(new Transition(49, 49, state5));
		state3.addTransition(new Transition(48, 48, state4));
		state3.addTransition(new Transition(49, 49, state5));
		state2.addTransition(new Transition(48, 48, state3));
		state2.addTransition(new Transition(49, 49, state3));
		state1.addTransition(new Transition(48, 48, state2));
		
		return Automaton.builder(state1).build();
	}
	
	
	@Test
	public void test2() {
		State a = new State();
		State b = new State();
		State c = new State();
		State d = new State();
		State e = new State(StateType.FINAL);
		
		a.addTransition(new Transition('0', b));
		a.addTransition(new Transition('1', c));
		b.addTransition(new Transition('0', '1', d));
		c.addTransition(new Transition('0', '1', d));
		d.addTransition(new Transition('0', '1', e));
		
		Automaton minimized = Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton2(), minimized);
	}
	
	private Automaton getAutomaton2() {
		State state1 = new State();
		State state2 = new State();
		State state3 = new State();
		State state4 = new State(StateType.FINAL);
		state3.addTransition(new Transition(48, 49, state4));
		state2.addTransition(new Transition(48, 49, state3));
		state1.addTransition(new Transition(49, 49, state2));
		state1.addTransition(new Transition(48, 48, state2));
		return Automaton.builder(state1).build();
	}
	
	
	@Test
	public void test3() {
		State a = new State(StateType.FINAL);
		State b = new State();
		State c = new State(StateType.FINAL);
		State d = new State();
		State e = new State(StateType.FINAL);
		
		a.addTransition(new Transition('0', a));
		a.addTransition(new Transition('1', b));
		b.addTransition(new Transition('0', c));
		b.addTransition(new Transition('1', d));
		c.addTransition(new Transition('0', c));
		c.addTransition(new Transition('1', e));
		d.addTransition(new Transition('0', c));
		d.addTransition(new Transition('1', d));
		e.addTransition(new Transition('0', e));
		e.addTransition(new Transition('1', e));
		
		Automaton minimized = Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton3(), minimized);
	}
	
	private Automaton getAutomaton3() {
		State state1 = new State(StateType.FINAL);
		state1.addTransition(new Transition(48, 48, state1));
		State state2 = new State();
		State state3 = new State(StateType.FINAL);
		state3.addTransition(new Transition(48, 48, state3));
		state3.addTransition(new Transition(49, 49, state3));
		state2.addTransition(new Transition(48, 48, state3));
		state2.addTransition(new Transition(49, 49, state2));
		state1.addTransition(new Transition(49, 49, state2));
		return Automaton.builder(state1).build();
	}
	
	@Test
	public void test4() {
		State a = new State();
		State b = new State();
		State c = new State();
		State d = new State();
		State e = new State();
		State f = new State();
		State g = new State();
		State h = new State();
		State i = new State(StateType.FINAL);
		
		a.addTransition(new Transition('1', b));
		a.addTransition(new Transition('2', c));
		a.addTransition(new Transition('3', d));
		b.addTransition(new Transition('1', '3', e));
		c.addTransition(new Transition('1', '3', e));
		d.addTransition(new Transition('1', '3', e));
		e.addTransition(new Transition('1', f));
		e.addTransition(new Transition('2', g));
		e.addTransition(new Transition('3', h));
		f.addTransition(new Transition('1', '3', i));
		g.addTransition(new Transition('1', '3', i));
		h.addTransition(new Transition('1', i));
		
		Automaton minimized = Automaton.builder(a).minimize().build();
		
		assertEquals(getAutomaton4(), minimized);
	}
	
	private Automaton getAutomaton4() {
		State state1 = new State();
		State state2 = new State();
		State state3 = new State();
		State state4 = new State();
		State state5 = new State(StateType.FINAL);
		state4.addTransition(new Transition(49, 51, state5));
		state3.addTransition(new Transition(50, 50, state4));
		State state6 = new State();
		state6.addTransition(new Transition(49, 49, state5));
		state3.addTransition(new Transition(51, 51, state6));
		state3.addTransition(new Transition(49, 49, state4));
		state2.addTransition(new Transition(49, 51, state3));
		state1.addTransition(new Transition(50, 50, state2));
		state1.addTransition(new Transition(49, 49, state2));
		state1.addTransition(new Transition(51, 51, state2));
		return Automaton.builder(state1).build();
	}
	
}
