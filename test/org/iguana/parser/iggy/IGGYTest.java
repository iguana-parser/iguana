package org.iguana.parser.iggy;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class IGGYTest {
	
	@Test
	public void test() throws IOException {
		
		Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/examples/Example1.iggy"));
		
		Grammar grammar = Grammar.load(new File("test/org/iguana/parser/iggy/IGGY"));
		
		DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
		precedenceAndAssociativity.setOP2();
		
		grammar = new EBNFToBNF().transform(grammar);
		grammar = precedenceAndAssociativity.transform(grammar);
		grammar = new LayoutWeaver().transform(grammar);

		ParseResult result = Iguana.parse(input, grammar, Nonterminal.withName("Definition"));
		
		Assert.assertTrue(result.isParseSuccess());
		
		NonterminalNode sppf = result.asParseSuccess().getSPPFNode();
	}

}
