package org.iguana.parser.iggy.paper;

import iguana.parsetrees.iggy.TermTraversal;
import iguana.parsetrees.sppf.SPPFNode;
import iguana.parsetrees.term.*;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.iggy.GrammarBuilder;
import org.iguana.grammar.iggy.InlineRegex;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * @author Anastasia Izmaylova
 */
public class Tests {

    private static Grammar IGGY;
    private static Start start;

    @Before
    public void init() {
        IGGY = Grammar.load(getClass().getResourceAsStream("/IggyGrammar"));
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        IGGY = new EBNFToBNF().transform(IGGY);
        IGGY = precedenceAndAssociativity.transform(IGGY);
        IGGY = new LayoutWeaver().transform(IGGY);
        start = IGGY.getStartSymbol(Nonterminal.withName("Definition"));
    }

    @Test
    public void simple() {
        Grammar grammar = simpleGrammar();
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/Simple.txt"));
        GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, Nonterminal.withName("A"));

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        Term term = result.asParseSuccess().getTreeWithoutSharing();
        TermVisualization.generate(term, "test/org/iguana/parser/iggy/paper/graphs", "terms_simple");
    }

    public Grammar simpleGrammar() {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/Simple.iggy"));
        GrammarGraph graph = GrammarGraph.from(IGGY, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println("Could not read a Simple grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void xml() {
        Grammar grammar = transform(xmlGrammar());
        Start start = grammar.getStartSymbol(Nonterminal.withName("Start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/XML.txt"));
        GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        Term term = result.asParseSuccess().getTreeWithoutSharing();
        TermVisualization.generate(term, "test/org/iguana/parser/iggy/paper/graphs", "terms_xml");

        input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/BadXML.txt"));
        result = Iguana.parse(input, graph, start);
        Assert.assertTrue(result.isParseError());
    }

    public Grammar xmlGrammar() {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/XML.iggy"));
        GrammarGraph graph = GrammarGraph.from(IGGY, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println("Could not read an XML grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void ocaml() {
        Grammar grammar = transform(ocamlGrammar());
        Start start = grammar.getStartSymbol(Nonterminal.withName("start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/OCaml.txt"));
        GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        Term term = result.asParseSuccess().getTreeWithoutSharing();
        TermVisualization.generate(term, "test/org/iguana/parser/iggy/paper/graphs", "terms_ocaml");
    }

    public Grammar ocamlGrammar() {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/OCaml.iggy"));
        GrammarGraph graph = GrammarGraph.from(IGGY, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println("Could not read an OCaml grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void haskell() {
        Grammar grammar = transform(haskellGrammar());
        Start start = grammar.getStartSymbol(Nonterminal.withName("Start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/Haskell.txt"));
        GrammarGraph graph = GrammarGraph.from(grammar, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        Term term = result.asParseSuccess().getTreeWithoutSharing();
        TermVisualization.generate(term, "test/org/iguana/parser/iggy/paper/graphs", "terms_haskell");

        input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/BadHaskell.txt"));
        result = Iguana.parse(input, graph, start);
        Assert.assertTrue(result.isParseError());
    }

    public Grammar haskellGrammar() {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/Haskell.iggy"));
        GrammarGraph graph = GrammarGraph.from(IGGY, input, Configuration.DEFAULT);
        ParseResult result = Iguana.parse(input, graph, start);

        if (result.isParseError())
            System.out.println("Could not read a Haskell grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getCountAmbiguousNodes());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    private static Grammar getGrammar(SPPFNode sppf, Input input) {
        Term term = SPPFToTerms.convertNoSharing(sppf, new DefaultTermBuilder(input));
        GrammarBuilder builder = new GrammarBuilder();
        List<Rule> rules = (List<Rule>)TermTraversal.build(term, builder);
        Nonterminal layout = null;
        for (Rule rule : rules) {
            if (rule.getAttributes().containsKey("@Layout")) {
                layout = rule.getHead();
                break;
            }
        }
        return Grammar.builder().addRules(rules).setLayout(layout).build();
    }

    private static Grammar transform(Grammar grammar) {
        grammar = new InlineRegex().transform(grammar);

        DesugarAlignAndOffside alignAndOffside = new DesugarAlignAndOffside();
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        alignAndOffside.doAlign();
        precedenceAndAssociativity.setOP2();

        grammar = alignAndOffside.transform(grammar);
        grammar = new EBNFToBNF().transform(grammar);

        alignAndOffside.doOffside();
        grammar = alignAndOffside.transform(grammar);

        grammar = precedenceAndAssociativity.transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);

        return grammar;
    }

}
