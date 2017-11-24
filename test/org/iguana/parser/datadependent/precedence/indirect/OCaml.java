package org.iguana.parser.datadependent.precedence.indirect;

import java.io.File;
import java.util.List;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.iguana.util.IguanaRunner;
import org.iguana.util.RunResult;
import org.iguana.util.RunResultUtil;
import org.junit.Assert;
import org.junit.Test;

import iguana.utils.input.Input;

public class OCaml {
	
	@Test
    public void test() {

        Grammar grammar = Grammar.load(new File("/Users/afroozeh/OCaml1"));
		
		grammar = new EBNFToBNF().transform(grammar);
		
		DesugarPrecedenceAndAssociativity precedence = new DesugarPrecedenceAndAssociativity();
		precedence.setOP2();
		
		grammar = precedence.transform(grammar);
		// System.out.println(grammar.toString());
		
		grammar = new LayoutWeaver().transform(grammar);

		List<RunResult> results = IguanaRunner.builder(grammar, Nonterminal.withName("CompilationUnit"))
				.addDirectory("/Users/afroozeh/workspace/ocaml", "ml", true)
				.build()
				.run();

		System.out.println(RunResultUtil.format(results));

//		Input input = Input.fromFile(new File("/Users/afroozeh/workspace/iguana/test/org/iguana/parser/datadependent/precedence/indirect/Test.ml"));
//
//		GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
//
//        // Visualization.generateGrammarGraph("test/org/iguana/parser/datadependent/precedence/indirect/", graph);
//
//        ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("CompilationUnit"));
//
//        Assert.assertTrue(result.isParseSuccess());
//
//        // Visualization.generateSPPFGraph("test/org/iguana/parser/datadependent/precedence/indirect/",
//        //                   result.asParseSuccess().getSPPFNode(), input);
//
//        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());
//
//        System.out.println(result.asParseSuccess().getStatistics());
		
	}

}
