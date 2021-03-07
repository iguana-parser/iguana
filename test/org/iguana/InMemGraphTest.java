package org.iguana;

import iguana.utils.input.Edge;
import iguana.utils.input.GraphInput;
import iguana.utils.input.InMemGraphInput;
import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.sppf.NonterminalNode;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;

public class InMemGraphTest {

    @Test
    public void testGraphInput() {
        List<List<Edge>> edges = Arrays.asList(
                Arrays.asList(
                        new Edge("a", 1),
                        new Edge("b", 3)
                ),
                Collections.singletonList(
                        new Edge("a", 2)
                ),
                Collections.singletonList(
                        new Edge("a", 0)
                ),
                Collections.singletonList(
                        new Edge("b", 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Arrays.asList(0, 1),
                Arrays.asList(0, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);

        Map<Pair, ParseTreeNode> node = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }

    @Test
    public void testGraphReachability1() {
        List<List<Edge>> edges = Arrays.asList(
                Arrays.asList(
                        new Edge("a", 1),
                        new Edge("b", 3)
                ),
                Collections.singletonList(
                        new Edge("a", 2)
                ),
                Collections.singletonList(
                        new Edge("a", 0)
                ),
                Collections.singletonList(
                        new Edge("b", 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Arrays.asList(0, 1),
                Arrays.asList(0, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test2/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        List<Pair> parseResults = parser.getReachabilities(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }

    @Test
    public void testGraphReachability2() {
        List<List<Edge>> edges = Arrays.asList(
                Arrays.asList(
                        new Edge("a", 1),
                        new Edge("b", 3)
                ),
                Collections.singletonList(
                        new Edge("a", 2)
                ),
                Collections.singletonList(
                        new Edge("a", 0)
                ),
                Collections.singletonList(
                        new Edge("b", 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Collections.singletonList(2),
                Collections.singletonList(3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test3/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        List<Pair> parseResults = parser.getReachabilities(input, new ParseOptions.Builder().setAmbiguous(true).build());
        Map<Pair, NonterminalNode> parseResults1 = parser.getSPPF(input, new ParseOptions.Builder().setAmbiguous(true).build());

        assert parseResults.size() == parseResults1.size();

        //  Set<Pair> pairs = parseResults;
        Set<Pair> pairs1 = parseResults1.keySet();
        assert parseResults.containsAll(pairs1);

    }

    @Test
    public void testGraphReachability3() {
        List<List<Edge>> edges = Collections.singletonList(
                Arrays.asList(
                        new Edge("a", 0),
                        new Edge("b", 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Collections.singletonList(0),
                Collections.singletonList(0)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test3/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        List<Pair> parseResults = parser.getReachabilities(input, new ParseOptions.Builder().setAmbiguous(true).build());
        Map<Pair, NonterminalNode> parseResults1 = parser.getSPPF(input, new ParseOptions.Builder().setAmbiguous(true).build());

        assert parseResults.size() == parseResults1.size();

        Set<Pair> pairs1 = parseResults1.keySet();
        assert parseResults.containsAll(pairs1);;

    }

//    @Test
//    public void testGraphReachability4() {
//        List<List<Edge>> edges = Arrays.asList(
//                Collections.singletonList(
//                        new Edge("a", 1)
//                ),
//                Collections.singletonList(
//                        new Edge("a", 2)
//                ),
//                Arrays.asList(
//                        new Edge("a", 0),
//                        new Edge("b", 3)
//                ),
//                Collections.singletonList(
//                        new Edge("b", 2)
//                )
//        );
//        GraphInput input = new InMemGraphInput(
//                edges,
//                Arrays.asList(0, 1, 2),
//                Arrays.asList(2, 3)
//        );
//
//        Grammar grammar;
//        try {
//            grammar = Grammar.load("test/resources/grammars/graph/Test5/grammar.json", "json");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("No grammar.json file is present");
//        }
//        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);
//
//        List<Pair> parseResults = parser.getReachabilities(input, new ParseOptions.Builder().setAmbiguous(true).build());
//        Map<Pair, NonterminalNode> parseResults1 = parser.getSPPF(input, new ParseOptions.Builder().setAmbiguous(true).build());
//
//        assert parseResults.size() == parseResults1.size();
//
//        Set<Pair> pairs1 = parseResults1.keySet();
//        assert parseResults.containsAll(pairs1);
//
//    }

    @Test
    public void testGraphInput2() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("a", 1)
                )
        );
        Input input = new InMemGraphInput(
                edges,
                Collections.singletonList(0),
                Collections.singletonList(1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test2/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        ParseTreeNode parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }
}
