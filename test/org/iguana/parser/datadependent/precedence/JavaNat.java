package org.iguana.parser.datadependent.precedence;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.patterns.ExceptPattern;
import org.iguana.grammar.patterns.PrecedencePattern;
import org.iguana.grammar.precedence.OperatorPrecedence;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.iguana.util.Configuration.EnvironmentImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class JavaNat {
	
	DesugarPrecedenceAndAssociativity desugarPrecedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();

    @Test
    public void test() throws IOException {
         Grammar grammar = Grammar.load(this.getClass().getResourceAsStream("./JavaNatChar"));
         
         grammar = new EBNFToBNF().transform(grammar);
         
         desugarPrecedenceAndAssociativity.setOP1();
         
		 Grammar grammar1 = desugarPrecedenceAndAssociativity.transform(grammar);
         // System.out.println(grammar1);
         
         grammar1 = new LayoutWeaver().transform(grammar1);
         
         Grammar grammar2 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/JavaNatChar"));
         List<PrecedencePattern> precedencePatterns = grammar2.getPrecedencePatterns();
		 List<ExceptPattern> exceptPatterns = grammar2.getExceptPatterns();
         
         grammar2 = new EBNFToBNF().transform(grammar2);
         
		 grammar2 = new OperatorPrecedence(precedencePatterns, exceptPatterns).transform(grammar2);
         
         // System.out.println(grammar2);
         
         grammar2 = new LayoutWeaver().transform(grammar2);
         
         desugarPrecedenceAndAssociativity.setOP2();
         
         Grammar grammar3 = desugarPrecedenceAndAssociativity.transform(grammar);
         // System.out.println(grammar3);
         
         grammar3 = new LayoutWeaver().transform(grammar3);
         
         Grammar grammar4 = Grammar.load(new File("test/org/iguana/parser/datadependent/precedence/JavaSpecChar"));
         
         grammar4 = new EBNFToBNF().transform(grammar4);
         grammar4 = new LayoutWeaver().transform(grammar4);

         Input input = Input.fromFile(new File("src/org/iguana/util/hashing/hashfunction/MurmurHash2.java"));
         GrammarGraph graph1 = GrammarGraph.from(grammar1, input, Configuration.builder().setEnvironmentImpl(EnvironmentImpl.TRIE).build());
         GrammarGraph graph2 = GrammarGraph.from(grammar2, input, Configuration.DEFAULT);
         GrammarGraph graph3 = GrammarGraph.from(grammar3, input, Configuration.DEFAULT);
         GrammarGraph graph4 = GrammarGraph.from(grammar4, input, Configuration.DEFAULT);

         ParseResult result1 = Iguana.parse(input, graph1, Nonterminal.withName("CompilationUnit"));
         ParseResult result2 = Iguana.parse(input, graph2, Nonterminal.withName("CompilationUnit"));
         ParseResult result3 = Iguana.parse(input, graph3, Nonterminal.withName("CompilationUnit"));
         ParseResult result4 = Iguana.parse(input, graph4, Nonterminal.withName("CompilationUnit"));
         
         Assert.assertTrue(result1.isParseSuccess());
         Assert.assertTrue(result2.isParseSuccess());
         Assert.assertTrue(result3.isParseSuccess());
         Assert.assertTrue(result4.isParseSuccess());
         
         Assert.assertEquals(0, result1.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result2.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result3.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         Assert.assertEquals(0, result4.asParseSuccess().getStatistics().getCountAmbiguousNodes());
         
         System.out.println("OP scheme 1:");
         System.out.println(result1.asParseSuccess().getStatistics());
         System.out.println("Shape-preserving rewriting:");
         System.out.println(result2.asParseSuccess().getStatistics());
         System.out.println("OP scheme 2:");
         System.out.println(result3.asParseSuccess().getStatistics());
         System.out.println("Standard rewriting:");
         System.out.println(result4.asParseSuccess().getStatistics());
    }
}
