package org.jgll.regex;

import java.util.List;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.util.CollectionsUtil;
import org.junit.Test;

public class PrefixAnalysisTest {

	@Test
	public void test() {
		Sequence<Character> k1 = Sequence.from("while");
		Sequence<Character> k2 = Sequence.from("if");
		Sequence<Character> k3 = Sequence.from("public");
		RegularExpression id = Plus.from(CharacterClass.from(CharacterRange.in('a', 'z')));
		
		List<RegularExpression> list = CollectionsUtil.list(k1, k2, k3, id);
		
		System.out.println(RegularExpressionsUtil.addFollowRestrictions(list));
	}
	
}
