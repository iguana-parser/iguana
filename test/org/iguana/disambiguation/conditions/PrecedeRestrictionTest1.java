package org.iguana.disambiguation.conditions;

import static org.junit.Assert.*;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.SPPFNode;
import org.iguana.sppf.SPPFNodeFactory;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= "for" L? Id | "forall"
 * 
 * Id ::= [a-z] !<< [a-z]+ !>> [a-z]
 * 
 * L ::= " "
 * 
 * @author Ali Afroozeh
 * 
 */
public class PrecedeRestrictionTest1 {

	private Grammar grammar;
	
	private Nonterminal S = Nonterminal.withName("S");
	private RegularExpression forr = Terminal.from(Sequence.from("for"));
	private RegularExpression forall = Terminal.from(Sequence.from("forall"));
	private Nonterminal L = Nonterminal.withName("L");
	private Nonterminal Id = Nonterminal.withName("Id");
	private Character ws = Character.from(' ');
	private CharacterRange az = CharacterRange.in('a', 'z');
	
	private Plus AZPlus = Plus.builder(az).addPostCondition(RegularExpressionCondition.notFollow(az))
			                              .addPreCondition(RegularExpressionCondition.notPrecede(az)).build();

	@Before
	public void init() {
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
		assertTrue(result.asParseSuccess().getRoot().deepEquals(getExpectedSPPF(parser.getGrammarGraph())));
	}

	private SPPFNode getExpectedSPPF(GrammarGraph graph) {
		SPPFNodeFactory factory = new SPPFNodeFactory(graph);
		NonterminalNode node1 = factory.createNonterminalNode("S", 0, 6);
		PackedNode node2 = factory.createPackedNode("S ::= (f o r a l l) .", 6, node1);
		TerminalNode node3 = factory.createTerminalNode("(f o r a l l)", 0, 6);
		node2.addChild(node3);
		node1.addChild(node2);		
		return node1;
	}

}
