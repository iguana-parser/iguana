package org.jgll.parser.datadependent.ebnf;

import static org.jgll.datadependent.ast.AST.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Code;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.regex.Alt;
import org.jgll.regex.Sequence;
import org.jgll.regex.Star;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Anastasia Izmaylova
 *
 * X ::= A s:(b:B | c:C print(s.lExt) d:D)*
 * 
 * A ::= a
 * B ::= b
 * C ::= c
 * D ::= d
 *
 */

public class Test2 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		Nonterminal D = Nonterminal.withName("D");
		
		
		Rule r1 = Rule.withHead(X)
					.addSymbol(Nonterminal.builder(A).build())
					.addSymbol(Star.builder(Alt.builder(Nonterminal.builder(B).setLabel("b").build(),
														Sequence.builder(Code.code(Nonterminal.builder(C).setLabel("c").build(),
																				   stat(println(lExt("s")))),
																         Nonterminal.builder(D).setLabel("d").build()).build()).build())
									.setLabel("s").build()).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		Rule r4 = Rule.withHead(C).addSymbol(Character.from('c')).build();
		Rule r5 = Rule.withHead(D).addSymbol(Character.from('d')).build();
		
		grammar = Grammar.builder().addRules(r1, r2, r3, r4, r5).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
// 		FIXME: Graph builder for Code symbol + EBNF translation
		
//		Input input = Input.fromString("acdbcd");
//		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
//		
//		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
//		
//		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
//		ParseResult result = parser.parse(input, graph, Nonterminal.withName("X"));
//		
//		if (result.isParseSuccess()) {
//			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
//					result.asParseSuccess().getRoot(), input);
//		}
		
	}

}
