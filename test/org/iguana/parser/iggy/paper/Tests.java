package org.iguana.parser.iggy.paper;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.DesugarAlignAndOffside;
import org.iguana.grammar.transformation.DesugarPrecedenceAndAssociativity;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.iggy.InlineRegex;
import org.iguana.parser.Iguana;
import org.iguana.parser.ParseResult;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
        start = Start.from(Nonterminal.withName("Definition"));
    }

    @Test
    public void simple() throws IOException {
        Grammar grammar = simpleGrammar();
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/Simple.txt"));
        ParseResult result = Iguana.parse(input, grammar, Nonterminal.withName("A"));

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());
    }

    public Grammar simpleGrammar() throws IOException {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/Simple.iggy"));
        ParseResult result = Iguana.parse(input, IGGY);

        if (result.isParseError())
            System.out.println("Could not read a Simple grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void xml() throws IOException {
        Grammar grammar = transform(xmlGrammar());
        Start start = Start.from(Nonterminal.withName("Start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/XML.txt"));
        ParseResult result = Iguana.parse(input, grammar);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/BadXML.txt"));
        result = Iguana.parse(input, grammar);
        Assert.assertTrue(result.isParseError());
    }

    public Grammar xmlGrammar() throws IOException {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/XML.iggy"));
        ParseResult result = Iguana.parse(input, IGGY);

        if (result.isParseError())
            System.out.println("Could not read an XML grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void ocaml() throws IOException {
        Grammar grammar = transform(ocamlGrammar());
        Start start = Start.from(Nonterminal.withName("start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/OCaml.txt"));
        ParseResult result = Iguana.parse(input, grammar);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());
    }

    public Grammar ocamlGrammar() throws IOException {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/OCaml.iggy"));
        ParseResult result = Iguana.parse(input, IGGY);

        if (result.isParseError())
            System.out.println("Could not read an OCaml grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    @Test
    public void haskell() throws IOException {
        Grammar grammar = transform(haskellGrammar());
        Start start = Start.from(Nonterminal.withName("Start"));
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/Haskell.txt"));
        ParseResult result = Iguana.parse(input, grammar);

        if (result.isParseError())
            System.out.println(result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/inputs/BadHaskell.txt"));
        result = Iguana.parse(input, grammar);
        Assert.assertTrue(result.isParseError());
    }

    public Grammar haskellGrammar() throws IOException {
        Input input = Input.fromFile(new File("test/org/iguana/parser/iggy/paper/grammars/Haskell.iggy"));
        ParseResult result = Iguana.parse(input, IGGY);

        if (result.isParseError())
            System.out.println("Could not read a Haskell grammar: " + result.asParseError());

        Assert.assertTrue(result.isParseSuccess());
        Assert.assertEquals(0, result.asParseSuccess().getStatistics().getAmbiguousNodesCount());

        return getGrammar(result.asParseSuccess().getSPPFNode(), input);
    }

    private static Grammar getGrammar(NonterminalNode sppf, Input input) {
//        Term term = SPPFToTerms.convertNoSharing(sppf, new DefaultTermBuilder(input));
//        GrammarBuilder builder = new GrammarBuilder();
//        List<Rule> rules = (List<Rule>)TermTraversal.build(term, builder);
//        Nonterminal layout = null;
//        for (Rule rule : rules) {
//            if (rule.getAttributes().containsKey("@Layout")) {
//                layout = rule.getHead();
//                break;
//            }
//        }
//        return Grammar.builder().addRules(rules).setLayout(layout).build();
        return null;
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
