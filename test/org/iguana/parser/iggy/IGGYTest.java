package org.iguana.parser.iggy;

import java.io.File;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.tree.TermBuilder;
import iguana.parsetrees.tree.Tree;
import iguana.parsetrees.tree.TreeBuilderFactory;
import iguana.parsetrees.tree.TreeVisualization;
import iguana.utils.input.Input;

public class IGGYTest {
	
	@Test
	public void test() {
		
		Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/examples/Example1.iggy"));
		
		Grammar grammar = Grammar.load(new File("test/org/iguana/parser/iggy/IGGY"));
		
		DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
		precedenceAndAssociativity.setOP2();
		
		grammar = new EBNFToBNF().transform(grammar);
		grammar = precedenceAndAssociativity.transform(grammar);
		grammar = new LayoutWeaver().transform(grammar);
		
		// System.out.println(grammar);
		// System.exit(0);
		
		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
		
		ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("Definition"));
		
		Assert.assertTrue(result.isParseSuccess());
		
		NonterminalNode sppf = result.asParseSuccess().getSPPFNode();
		
		Tree term = TermBuilder.build_no_memo(sppf, TreeBuilderFactory.getDefault(input));
		
		// TreeVisualization.generate(term, "test/org/iguana/parser/idea/", "terms");
	}

}
