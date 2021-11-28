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
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
                Stream.of(0, 1, 2, 3),
                Arrays.asList(0, 1, 2, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);

        Map<Pair, ParseTreeNode> node = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
        System.out.println(node.size());
        node.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));

    }
    @Test
    public void testGraphReachability1() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("a", 2)
                ),
                Collections.singletonList(
                        new Edge("a", 3)
                ),
                Collections.singletonList(
                        new Edge("a", 3)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3),
                Arrays.asList(0, 1, 2, 3)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test4/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability5() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("a", 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test4/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability6() {
        List<List<Edge>> edges = List.of(
                Arrays.asList(
                        new Edge("a", 0),
                        new Edge("b", 0)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0),
                List.of(0)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability10() {
        List<List<Edge>> edges = List.of(
                Arrays.asList(
                        new Edge("a", 2)
                ),
                Arrays.asList(
                        new Edge("b", 1),
                        new Edge("a", 0)
                ),
                Arrays.asList(
                        new Edge("a", 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2),
                List.of(0, 1, 2)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability9() {
        List<List<Edge>> edges = List.of(
                Arrays.asList(
                        new Edge("a", 1)
                ),
                Arrays.asList(
                        new Edge("a", 2),
                        new Edge("a", 5),
                        new Edge("b", 8)
                ),
                Arrays.asList(
                        new Edge("b", 3)
                ),
                Arrays.asList(
                        new Edge("b", 4)
                ),
                Arrays.asList(),
                Arrays.asList(
                        new Edge("b", 6)
                ),
                Arrays.asList(
                        new Edge("a", 7)
                ),
                Arrays.asList(
                        new Edge("b", 3)
                ),
                Arrays.asList(
                        new Edge("a", 9)
                ),
                Arrays.asList(
                        new Edge("b", 10)
                ),
                Arrays.asList(
                        new Edge("a", 3)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test5/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability7() {
        List<List<Edge>> edges = Arrays.asList(
                List.of(
                        new Edge("a", 1)
                ),
                List.of(
                        new Edge("a", 2)
                ),
                Arrays.asList(
                        new Edge("b", 3),
                        new Edge("b", 3)
                ),
                List.of(
                        new Edge("a", 4)
                ),
                List.of()
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4),
                List.of(0, 1, 2, 3, 4)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability8() {
        List<List<Edge>> edges = Arrays.asList(
                List.of(
                        new Edge("a", 1)
                ),
                List.of(
                        new Edge("a", 2)
                ),
                List.of(
                        new Edge("a", 3)
                ),
                List.of(
                        new Edge("b", 4)
                ),
                List.of(
                        new Edge("b", 5)
                ),
                List.of(
                        new Edge("a", 6)
                ),
                List.of(
                        new Edge("b", 6)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1, 2, 3, 4, 5, 6),
                List.of(0, 1, 2, 3, 4, 5, 6)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g1/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
//        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

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
                Stream.of(0),
                Collections.singletonList(1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/g2/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        ParseTreeNode parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }

    @Test
    public void testGraphReachability3() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.singletonList(
                        new Edge("b", 1)
                )
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test5/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }

    @Test
    public void testGraphReachability4() {
        List<List<Edge>> edges = Arrays.asList(
                Collections.singletonList(
                        new Edge("a", 1)
                ),
                Collections.emptyList()
        );
        GraphInput input = new InMemGraphInput(
                edges,
                Stream.of(0, 1),
                Arrays.asList(0, 1)
        );

        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test4/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
//        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseResults = parser.getParserTree(input,
                new ParseOptions.Builder().setAmbiguous(true).build());
        assert (parseResults != null);
        System.out.println(parseResults.size());
        parseResults.keySet().forEach(System.out::println);
        parseResults.keySet().forEach(x -> System.out.println(x.startVertex + " " + x.endVertex));
    }
}
