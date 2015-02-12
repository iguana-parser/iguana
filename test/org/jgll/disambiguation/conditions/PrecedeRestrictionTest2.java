package org.jgll.disambiguation.conditions;

import static org.junit.Assert.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterRange;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.Opt;
import org.jgll.regex.Plus;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.Sequence;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.SPPFNodeFactory;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= "for" L? Id | "forall"
 * 
 * Id ::= "for" !<< [a-z]+ !>> [a-z]
 * 
 * L ::= " "
 * 
 * @author Ali Afroozeh
 * 
 */
public class PrecedeRestrictionTest2 {
	
	private Nonterminal S = Nonterminal.withName("S");
	private RegularExpression forr = Terminal.from(Sequence.from("for"));
	private RegularExpression forall = Terminal.from(Sequence.from("forall"));
	private Nonterminal L = Nonterminal.withName("L");
	private Nonterminal Id = Nonterminal.withName("Id");
	private Character ws = Character.from(' ');
	private CharacterRange az = CharacterRange.in('a', 'z');
	private Grammar grammar;
	private Plus AZPlus = Plus.builder(az).addPreCondition(RegularExpressionCondition.notFollow(az))
			                              .addPostCondition(RegularExpressionCondition.notPrecede(forr)).build();

	@Before
	public void createGrammar() {
		Rule r1 = Rule.withHead(S).addSymbols(forr, Opt.from(L), Id).build();
		Rule r2 = Rule.withHead(S).addSymbol(forall).build();
		Rule r3 = Rule.withHead(Id).addSymbol(AZPlus).build();
		Rule r4 = Rule.withHead(L).addSymbol(ws).build();

		grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();
	}

	@Test
	public void test() {
		Input input = Input.fromString("forall");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, Nonterminal.withName("S"));
		assertTrue(result.isParseSuccess());
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getRegistry())));
	}

	private SPPFNode getExpectedSPPF(GrammarRegistry registry) {
		SPPFNodeFactory factory = new SPPFNodeFactory(registry);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 6);
		PackedNode node2 = factory.createPackedNode("S ::= (f o r a l l) .", 6, node1);
		TerminalNode node3 = factory.createTerminalNode("(f o r a l l)", 0, 6);
		node2.addChild(node3);
		node1.addChild(node2);
		return node1;
	}

}
