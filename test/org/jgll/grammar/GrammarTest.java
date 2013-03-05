package org.jgll.grammar;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import static junit.framework.Assert.*;

public class GrammarTest {

	@Test
	public void gamma2FirstAndFollowSets() {
		Grammar gamma2 = SampleGrammars.gamma2();
		gamma2.calculateFirstAndFollowSets();
		assertEquals(set(new Character('a'), Epsilon.getInstance()), gamma2.getNonterminalByName("S").getFirstSet());
		assertEquals(set(new Character('a')), gamma2.getNonterminalByName("A").getFirstSet());
		assertEquals(true, gamma2.getNonterminalByName("S").isNullable());
		assertEquals(false, gamma2.getNonterminalByName("A").isNullable());
	}
	
	@Test
	public void leftFactorizedArithmeticExpressions() {
		Grammar leftFactorized = SampleGrammars.leftFactorizedArithmeticExpressions();
		leftFactorized.calculateFirstAndFollowSets();
		assertEquals(set(new Character('('), new Character('a')), leftFactorized.getNonterminalByName("E").getFirstSet());
		assertEquals(set(new Character('+'), Epsilon.getInstance()), leftFactorized.getNonterminalByName("E1").getFirstSet());
		assertEquals(set(new Character('*'), Epsilon.getInstance()), leftFactorized.getNonterminalByName("T1").getFirstSet());
		assertEquals(set(new Character('('), new Character('a')), leftFactorized.getNonterminalByName("T").getFirstSet());
		assertEquals(set(new Character('('), new Character('a')), leftFactorized.getNonterminalByName("F").getFirstSet());
	}
	
	@SafeVarargs
	private static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}

}
