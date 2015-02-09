package org.jgll.parser.datadependent.ebnf;

import static org.jgll.datadependent.ast.AST.*;
import static org.jgll.grammar.condition.DataDependentCondition.predicate;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CodeBlock;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
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
 * X ::= a:A b:(b:B [b.lExt >= a.rExt] print(b.lExt) c:C print(c))*  // shadowing (b:B)
 * 
 * A ::= a
 * B ::= b
 * C ::= c
 *
 */

public class Test5 {
	
	private Grammar grammar;

	@Before
	public void init() {
		
		Nonterminal X = Nonterminal.withName("X");
		
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		Nonterminal C = Nonterminal.withName("C");
		
		Rule r1 = Rule.withHead(X)
					.addSymbol(Nonterminal.builder(A).setLabel("a").build())
					.addSymbol(Star.builder(Sequence.builder(Nonterminal.builder(B).setLabel("b")
																.addPreCondition(predicate(greaterEq(lExt("b"), rExt("a")))).build(),
															 CodeBlock.code(stat(println(lExt("b")))),
															 Nonterminal.builder(C).setLabel("c").build(),
															 CodeBlock.code(stat(println(var("c"))))).build())
									.setLabel("b").build()).build();
		
		Rule r2 = Rule.withHead(A).addSymbol(Character.from('a')).build();
		Rule r3 = Rule.withHead(B).addSymbol(Character.from('b')).build();
		Rule r4 = Rule.withHead(C).addSymbol(Character.from('c')).build();
		
		grammar = Grammar.builder().addRules(r1, r2, r3, r4).build();
		
	}
	
	@Test
	public void test() {
		System.out.println(grammar);
		
		Input input = Input.fromString("abcbcbc");
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);
		
		Visualization.generateGrammarGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", graph);
		
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, graph, Nonterminal.withName("X"));
		
		if (result.isParseSuccess()) {
			Visualization.generateSPPFGraph("/Users/anastasiaizmaylova/git/diguana/test/org/jgll/parser/datadependent/", 
					result.asParseSuccess().getRoot(), input);
		}
		
	}

}
