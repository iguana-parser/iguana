package org.iguana.parser.datadependent.precedence.indirect;

import java.io.File;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Test;

import iguana.utils.input.Input;

public class OCaml {
	
	@Test
    public void test() {
		
		Grammar grammar = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/indirect/OCaml"));
		
		grammar = new EBNFToBNF().transform(grammar);
		
		DesugarPrecedenceAndAssociativity precedence = new DesugarPrecedenceAndAssociativity();
		precedence.setOP2();
		
		grammar = precedence.transform(grammar);
		// System.out.println(grammar.toString());
		
		grammar = new LayoutWeaver().transform(grammar);
		
		Input input = Input.fromFile(new File("test/org/iguana/parser/datadependent/precedence/indirect/Test.ml"));
		
		GrammarGraph graph = grammar.toGrammarGraph(input, Configuration.DEFAULT);

        // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/indirect/", graph);

        GLLParser parser = ParserFactory.getParser();
        ParseResult result = parser.parse(input, graph, Nonterminal.withName("CompilationUnit"));

        Assert.assertTrue(result.isParseSuccess());

        // Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/indirect/",
        //                   result.asParseSuccess().getSPPFNode(), input);

        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
        
        System.out.println(result.asParseSuccess().getStatistics());
		
	}

}
