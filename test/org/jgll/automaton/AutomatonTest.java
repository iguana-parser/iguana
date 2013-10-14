package org.jgll.automaton;

import org.junit.Test;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;

public class AutomatonTest {

	@Test
	public void test() {

		String s1 = new String(Character.toChars(1));
		String e1 = new String(Character.toChars(38));
		
		String s2 = new String(Character.toChars(40));
		String e2 = new String(Character.toChars(91));

		String s3 = new String(Character.toChars(93));
		
		String e3 = new String(Character.toChars(200));


		String pattern = "[" + s1 + "-" + e1 + s2 + "-" + e2 + s3 + "-" + e3 + "]";
//		String pattern = "[\u0001-\u0038 \u0040-\u0091 \u0093-\u0200]";
		System.out.println(pattern);
		RegExp r = new RegExp(pattern);
		Automaton a = r.toAutomaton();
 		RunAutomaton ra = new RunAutomaton(a);
		
		String s = "B";
			
		 
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
