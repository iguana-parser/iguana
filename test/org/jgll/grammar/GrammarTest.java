package org.jgll.grammar;

import org.junit.Test;

public class GrammarTest {

	@Test
	public void testNullables() {
		Grammar gamma2 = SampleGrammars.gamma2();
		for(GrammarSlot slot : gamma2.getGrammarSlots()) {
			System.out.println(slot);
		}
	}

}
