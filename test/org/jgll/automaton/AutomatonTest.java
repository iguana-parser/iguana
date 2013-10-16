package org.jgll.automaton;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

public class AutomatonTest {

	@Test
	public void test() {

		String pattern = "(([\\--\\-]?[0-9][0-9_-_]*[\\.-\\.][0-9_-_]*)|([\\--\\-]?[0-9][0-9_-_]*[\\.-\\.][0-9_-_]*[E-Ee-e][+-+\\--\\-]?[0-9][0-9_-_]*))";
		System.out.println(pattern);
		RegExp r = new RegExp(pattern);
		Automaton a = r.toAutomaton();
 		RunAutomaton ra = new RunAutomaton(a);
		
		String s = "1.2";
			
		 
		boolean run = ra.run(s);
		System.out.println(run);
		
//		int state = 0;
//		for(int i = 0; i < 5; i++) {
//			state = ra.step(state, s.charAt(i));
//		}
//		
//		System.out.println(state);
	}
	
}
