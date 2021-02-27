//package org.iguana;
//
//import iguana.utils.input.GraphInput;
//import org.iguana.grammar.Grammar;
//import org.iguana.parser.IguanaParser;
//import org.iguana.parser.Pair;
//import org.iguana.parser.ParseOptions;
//import org.iguana.parsetree.ParseTreeNode;
//import org.neo4j.configuration.GraphDatabaseSettings;
//import org.neo4j.dbms.api.DatabaseManagementService;
//import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
//import org.neo4j.graphdb.Direction;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Relationship;
//import org.neo4j.graphdb.RelationshipType;
//import org.scalameter.Context;
//import org.scalameter.japi.ContextBuilder;
//import org.scalameter.japi.JBench;
//import org.scalameter.japi.JGen;
//import org.scalameter.japi.annotation.benchmark;
//import org.scalameter.japi.annotation.curve;
//import org.scalameter.japi.annotation.gen;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Map;
//import java.util.function.BiFunction;
//
//import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
//
//interface Data {
//    File databaseDirectory = new File("/Users/annavlasova/Downloads/neo4j-enterprise-4.0.4_2");
//    DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
//            .setConfig(GraphDatabaseSettings.read_only, true)
//            .build();
//    GraphDatabaseService graphDb = managementService.database(DEFAULT_DATABASE_NAME);
//    //        private static String relationshipName = "skos__broaderTransitive";
//    String relationshipName = "skos__narrowerTransitive";
//
//    static BiFunction<Relationship, Direction, String> getF() {
//        return (rel, direction) -> {
//            if (rel.isType(RelationshipType.withName(relationshipName))) {
//                if (direction.equals(Direction.INCOMING)) {
//                    return "a";
//                } else if (direction.equals(Direction.OUTGOING)) {
//                    return "b";
//                } else {
//                    throw new RuntimeException("Unexpected direction");
//                }
//            }
//            return null;
//        };
//    }
//
//    static Grammar getGrammar() {
//        Grammar grammar;
//        try {
//            grammar = Grammar.load("test/resources/grammars/graph/Test3/grammar.json", "json");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("No grammar.json file is present");
//        }
//        return grammar;
//    }
//}
//
//public class Neo4jBenchMark2 extends JBench.OfflineReport {
//    public final JGen<Integer> sizes = JGen.range("iters", 1, 20, 1);
//
//    @Override
//    public Context defaultConfig() {
//        return new ContextBuilder()
//                .put("exec.benchRuns", 25)
////                .put("exec.independentSamples", 2)
//                .build();
//    }

//    public static void main(String[] args) throws FileNotFoundException {
//        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
//                .setConfig(GraphDatabaseSettings.read_only, true)
//                .build();
//
//        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
//        testNeo4jGraphInput();
//    }

//    @AfterClass
//    public static void finalizeDb() {
//        removeData();
//        managementService.shutdown();
//    }

//    @Before
//    public void clearDb() {
//        removeData();
//    }

//    public static void testNeo4jGraphInput() throws FileNotFoundException {
//
//        Map<Integer, Integer> numPaths = new HashMap<>();
//        Map<Integer, List<Integer>> vertexToTime = new HashMap<>();
//        Grammar grammar;
//        try {
//            grammar = Grammar.load("test/resources/grammars/graph/Test3/grammar.json", "json");
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("No grammar.json file is present");
//        }
//
//        Map<Integer, Integer> counterLength = new HashMap<>();
////        for (int start = 0; start < 2; start += 1) { // for enzyme
////            GraphInput input = new Neo4jTestInput(graphDb, f, start);
//            IguanaParser parser = new IguanaParser(grammar);
//
////            Map<Pair, ParseTreeNode> parseTreeNodes = t(parser, input);
////                Map<Pair, ParseTreeNode> parseTreeNodes = parser.getParserTree(input,
////                        new ParseOptions.Builder().setAmbiguous(true).build());
////            if (iter >= iterNum / 2 && parseTreeNodes != null) {
////
////                if (iter == iterNum - 1) {
////                    numPaths.put(start, countNumberOfPaths(parseTreeNodes, counterLength));
////                }
////            }
//        }

//        System.out.println(counterLength);
//        Map<Integer, Double> resultTime = new HashMap<>();
//
//        vertexToTime.forEach((vertex, list) ->
//                resultTime.put(vertex, list.stream().mapToInt(x -> x).average().getAsDouble()));
//
//        PrintWriter out = new PrintWriter("enzyme_nt2.csv");
//        numPaths.keySet().forEach(vertex -> {
//            out.println(vertex + "," + numPaths.get(vertex) + "," + resultTime.get(vertex));
//        });
//
//        out.close();

//    }

//    private static int countNumberOfPaths(Map<Pair, ParseTreeNode> parseTreeNodes, Map<Integer, Integer> counter) {
//        int res = 0;
//        Map<ParseTreeNode, Integer> nodeToCurPaths = new HashMap<>();
//        Map<ParseTreeNode, List<Integer>> nodeToLength = new HashMap<>();
//
//        for (Pair verticesPair : parseTreeNodes.keySet()) {
//            ParseTreeNode parseTreeNode = parseTreeNodes.get(verticesPair);
//            traverse(parseTreeNode, nodeToCurPaths, nodeToLength);
//            res += nodeToCurPaths.get(parseTreeNode);
//
//            nodeToLength.get(parseTreeNode).forEach(length -> {
//                counter.putIfAbsent(length, 0);
//                counter.put(length, counter.get(length) + 1);
//            });
//        }
//        return res;
//    }
//
//    private static void traverse(ParseTreeNode parseTreeNode, Map<ParseTreeNode, Integer> nodeToCurPaths, Map<ParseTreeNode, List<Integer>> nodeToLengths) {
//        if (nodeToCurPaths.containsKey(parseTreeNode)) {
//            return;
//        }
//
//        List<ParseTreeNode> children = parseTreeNode.children();
//        int result;
//
//        List<Integer> lengths = new ArrayList<>();
//        if (parseTreeNode instanceof DefaultTerminalNode) {
////                || children.stream().allMatch(node -> node instanceof DefaultTerminalNode)) {
//            result = 1;
//            lengths.add(1);
//        } else if (parseTreeNode instanceof NonterminalNode) {
//            int curRes = 1;
//            lengths.add(0);
//            List<Integer> temp = new ArrayList<>();
//            for (ParseTreeNode childNode : children) {
//                traverse(childNode, nodeToCurPaths, nodeToLengths);
//                curRes *= nodeToCurPaths.get(childNode);
//                temp.clear();
//                nodeToLengths.get(childNode)
//                        .forEach(newL -> lengths.forEach(prevL -> temp.add(newL + prevL)));
//                lengths.clear();
//                lengths.addAll(temp);
//            }
//            result = curRes;
//        } else if (parseTreeNode instanceof AmbiguityNode) {
//            int curRes = 0;
//            for (ParseTreeNode childNode : children) {
//                traverse(childNode, nodeToCurPaths, nodeToLengths);
//                curRes += nodeToCurPaths.get(childNode);
//                lengths.addAll(nodeToLengths.get(childNode));
//            }
//            result = curRes;
//        } else {
//            throw new RuntimeException("Unexpected type of node");
//        }
//        nodeToCurPaths.put(parseTreeNode, result);
//        nodeToLengths.put(parseTreeNode, lengths);
//    }

    //    private static void removeData() {
//        try (Transaction tx = graphDb.beginTx()) {
//            tx.getAllNodes().forEach(node -> {
//                node.getRelationships().forEach(Relationship::delete);
//                node.delete();
//            });
//            tx.commit();
//        }
//    }
//    @gen("t")
//    @benchmark("t")
//    @curve("t")
//    public Map<Pair, ParseTreeNode> t() {
//        GraphInput input = new Neo4jTestInput(Data.graphDb, Data.getF(), 0);
//        IguanaParser parser = new IguanaParser(Data.getGrammar());
//        return parser.getParserTree(input,
//                new ParseOptions.Builder().setAmbiguous(true).build());
//    }
//}
//
