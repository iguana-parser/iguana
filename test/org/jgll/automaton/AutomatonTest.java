package org.jgll.automaton;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

public class AutomatonTest {

	@Test
	public void test() {

		String pattern = "[\"-\"](([\\-\\][\\ -\\ ])|[-!\\#-\\[\\]-?]|([\\-\\][\\n-\\n]))*[\"-\"]";
		System.out.println(pattern);
		RegExp r = new RegExp(pattern);
		Automaton a = r.toAutomaton();
 		RunAutomaton ra = new RunAutomaton(a);
		
		String s = "\"\"";
			
		 
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
