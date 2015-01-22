package org.jgll.parser.layout;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

public class LayoutTest1 {

	private static Grammar getGrammar() {
		Character a = Character.from('a');
		Character b = Character.from('b');
		Nonterminal S = Nonterminal.withName("S");
		Nonterminal A = Nonterminal.withName("A");
		Nonterminal B = Nonterminal.withName("B");
		
		Nonterminal L = Nonterminal.withName("L");
		
		Rule r1 = Rule.withHead(S).addSymbols(A, B).build();
		Rule r2 = Rule.withHead(A).addSymbol(a).build();
		Rule r3 = Rule.withHead(B).addSymbol(b).build();
		
		Rule layout = Rule.withHead(L).addSymbol(Character.from(' ')).build();
		
		return Grammar.builder().addLayout(layout).addRules(r1, r2, r3).build();
	}
	
	@Test
	public void test() {
		Input input = Input.fromString("a b");
		GrammarGraph grammarGraph = getGrammar().toGrammarGraph(input, Configuration.DEFAULT);
		Visualization.generateGrammarGraph("/Users/aliafroozeh/output", grammarGraph);
	}
}
