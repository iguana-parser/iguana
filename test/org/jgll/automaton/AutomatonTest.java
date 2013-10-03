package org.jgll.automaton;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

public class AutomatonTest {

	@Test
	public void test() {
		
		RegExp r = new RegExp("ab(c|d)*");
		Automaton a = r.toAutomaton();
		RunAutomaton ra = new RunAutomaton(a);
		
		String s = "abcdx";
		
		int state = 0;
		for(int i = 0; i < 5; i++) {
			state = ra.step(state, s.charAt(i));
		}
		
		System.out.println("Hi");
	}

}
