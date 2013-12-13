package org.jgll.grammar.symbols;

import static org.jgll.util.CollectionsUtil.list;
import static org.junit.Assert.assertTrue;

import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Range;
import org.junit.Test;


public class CharacterClassTest {
	
	@Test
	public void testContains() {
		CharacterClass c1 = new CharacterClass(list(new Range('a', 'z'), new Range('A', 'Z')));
		CharacterClass c2 = new CharacterClass(list(new Range('A', 'Z')));
		assertTrue(c1.contains(c2));
	}

}
