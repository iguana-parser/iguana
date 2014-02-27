package org.jgll.grammar.leftfactorization;


import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBank;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Character;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;


public class LeftFactorizationTest {

	@Test
	public void test() throws ParseError {
		GrammarBuilder builder = GrammarBank.arithmeticExpressions();
		
		builder.leftFactorize("E");
		
		Grammar grammar = builder.build();
		
		Input input = Input.fromString("a+a*a");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "E");
	}
	
	@Test
	public void test2() throws ParseError {
		Nonterminal S = new Nonterminal("S");
		Nonterminal A = new Nonterminal("A");
		Nonterminal B = new Nonterminal("B");
		Character a = new Character('a');
		Character b = new Character('b');
		Character c = new Character('c');
		
		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("test", factory);
		
		builder.addRule(new Rule(S, list(A, b)));
		builder.addRule(new Rule(S, list(A, c)));
		builder.addRule(new Rule(A, list(B)));
		builder.addRule(new Rule(B, list(a)));
		
		Grammar grammar = builder.build();
		
		Input input = Input.fromString("ab");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "S");		
	}
	
}
