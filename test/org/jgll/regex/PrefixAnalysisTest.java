package org.jgll.regex;

import java.util.List;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Range;
import org.jgll.util.CollectionsUtil;
import org.junit.Test;

public class PrefixAnalysisTest {

	@Test
	public void test() {
		Keyword k1 = new Keyword("while");
		Keyword k2 = new Keyword("if");
		Keyword k3 = new Keyword("public");
		RegularExpression id = new RegexPlus(new CharacterClass(Range.in('a', 'z')));
		
		List<RegularExpression> list = CollectionsUtil.list(k1, k2, k3, id);
		
		System.out.println(RegularExpressionsUtil.addFollowRestrictions(list));
	}
	
}
