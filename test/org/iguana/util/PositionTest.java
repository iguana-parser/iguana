package org.jgll.util;

import static org.junit.Assert.*;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class PositionTest {

	@org.junit.Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void test1() {
		Rule rule = Rule.withHead(Nonterminal.withName("A")).build();
		assertEquals("A ::= .", rule.getPosition(0).toString());
		
		exception.expect(IllegalArgumentException.class);
		rule.getPosition(1);
	}
	
	@Test
	public void test2() {
		// A ::= B
		Rule rule = Rule.withHead(Nonterminal.withName("A")).addSymbol(Nonterminal.withName("B")).build();
		assertEquals("A ::= . B", rule.getPosition(0).toString());
		assertEquals("A ::= B .", rule.getPosition(1).toString());
		
		exception.expect(IllegalArgumentException.class);
		rule.getPosition(2);
	}
	
}
