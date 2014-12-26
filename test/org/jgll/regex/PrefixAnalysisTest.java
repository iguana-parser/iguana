package org.jgll.regex;

import java.util.List;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.util.CollectionsUtil;
import org.junit.Test;

public class PrefixAnalysisTest {

	@Test
	public void test() {
		Keyword k1 = Keyword.from("while");
		Keyword k2 = Keyword.from("if");
		Keyword k3 = Keyword.from("public");
		RegularExpression id = RegexPlus.from(CharacterClass.from(CharacterRange.in('a', 'z')));
		
		List<RegularExpression> list = CollectionsUtil.list(k1, k2, k3, id);
		
		System.out.println(RegularExpressionsUtil.addFollowRestrictions(list));
	}
	
}
