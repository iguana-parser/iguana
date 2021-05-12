package benchmark;

import com.google.common.collect.Lists;
import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jBenchmarkInput;
import org.eclipse.collections.impl.list.Interval;
import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.AmbiguityNode;
import org.iguana.parsetree.DefaultTerminalNode;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;

import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class Neo4jBenchmark {
    private static final File databaseDirectory = new File("target/neo4j-hello-db");
    private static GraphDatabaseService graphDb;
    private static DatabaseManagementService managementService;

    private static final Map<String, String> relationshipNamesMap = new HashMap<>() {
        {
            put("nt", "narrowerTransitive");
            put("bt", "broaderTransitive");
        }
    };
    private static final String st = "st";

    //    args0 rel type (st/bt/nt)
    //    args1 rightNode
    //    args2 number of warm up iteration
    //    args3 total number of iterations
    //    args4 path to dataset
    //    args5 path to grammar
    //    args6 dataset name = name of file with results
    public static void main(String[] args) throws IOException {
        loadGraph(args[4]);
//        benchmark(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[5], args[6]);
        benchmarkReachabilities(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[5], args[6]);
        removeData();
        managementService.shutdown();
    }

    public static BiFunction<Relationship, Direction, String> getFunction(String relationshipName) {
        if (relationshipNamesMap.containsKey(relationshipName)) {
            return singleFunction(relationshipNamesMap.get(relationshipName));
        } else if (relationshipName.equals(st)) {
            return subclassAndTypeFunction();
        } else {
            throw new RuntimeException("Unknown relationship");
        }
    }

    public static BiFunction<Relationship, Direction, String> singleFunction(String relationshipName) {
        return (rel, direction) -> {
            if (rel.isType(RelationshipType.withName(relationshipName))) {
                if (direction.equals(Direction.INCOMING)) {
                    return "a";
                } else if (direction.equals(Direction.OUTGOING)) {
                    return "b";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            }
            return null;
        };
    }

    public static BiFunction<Relationship, Direction, String> subclassAndTypeFunction() {
        return (rel, direction) -> {
            if (rel.isType(RelationshipType.withName("subClassOf"))) {
                if (direction.equals(Direction.INCOMING)) {
                    return "a";
                } else if (direction.equals(Direction.OUTGOING)) {
                    return "b";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            } else if (rel.isType(RelationshipType.withName("type"))) {
                if (direction.equals(Direction.INCOMING)) {
                    return "c";
                } else if (direction.equals(Direction.OUTGOING)) {
                    return "d";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            }
            return null;
        };
    }

    public static void loadGraph(String pathToDataset) throws IOException {
        org.neo4j.io.fs.FileUtils.deleteRecursively(databaseDirectory);

        managementService =
                new DatabaseManagementServiceBuilder(databaseDirectory)
                        .setConfig(GraphDatabaseSettings.pagecache_memory, "100G")
                        .setConfig(GraphDatabaseSettings.pagecache_warmup_enabled, true)
                        .setConfig(BoltConnector.enabled, true)
                        .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687))
                        .build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);

        HashMap<Integer, Long> nodeList = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(pathToDataset))) {

            try (Transaction tx = graphDb.beginTx()) {
                stream.forEach(s -> {
                    String[] split = s.split("\\s+");

                    if (!nodeList.containsKey(Integer.parseInt(split[0]))) {
                        Node node1 = tx.createNode();
                        nodeList.put(Integer.parseInt(split[0]), node1.getId());
                        node1.addLabel(Label.label(split[0]));
                    }
                    if (!nodeList.containsKey(Integer.parseInt(split[2]))) {
                        Node node1 = tx.createNode();
                        nodeList.put(Integer.parseInt(split[2]), node1.getId());
                        node1.addLabel(Label.label(split[2]));
                    }
                    Node node1 = tx.getNodeById(nodeList.get(Integer.parseInt(split[0])));
                    Node node2 = tx.getNodeById(nodeList.get(Integer.parseInt(split[2])));
                    node1.createRelationshipTo(node2, RelationshipType.withName(split[1]));
                });
//                Iterable<Label> labels = tx.getAllLabels();
//                for (Label label : labels) {
//                    System.out.println("label: " + label);
//                    tx.findNodes(label).stream().forEach(System.out::println);
//                }
//                Iterable<RelationshipType> relationships = tx.getAllRelationshipTypes();
//                for (RelationshipType reltype : relationships) {
//                    System.out.println(reltype);
//                }
                tx.commit();
                tx.close();
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void benchmarkReachabilities(String relType, int rightNode, int warmUp, int maxIter, String pathToGrammar, String dataset) throws FileNotFoundException {
        BiFunction<Relationship, Direction, String> f = getFunction(relType);

        Map<String, List<Integer>> vertexToTime = new HashMap<>();
        Grammar grammar;
        try {
            grammar = Grammar.load(pathToGrammar, "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }

        PrintWriter outStatsTime = new PrintWriter("results/" + dataset + "_" + relType + "_time_reachabilities.csv");
        outStatsTime.append("chunk_size, time");
        outStatsTime.append("\n");
        List<Integer> chunkSize = Arrays.asList(1, 2, 4, 8, 16, 32, 50, 100, 500, 1000, 5000, 10000, rightNode);
        List<Integer> vertices = Interval.zeroTo(rightNode - 1);
        for (Integer sz : chunkSize) {
            List<List<Integer>> chunks = Lists.partition(vertices, sz);
            for (Integer iter = 0; iter < maxIter; iter++) {
                for (List<Integer> chunk : chunks) {
                    System.out.println("iter " + iter + " chunkSize " + sz);

                    GraphInput input = new Neo4jBenchmarkInput(graphDb, f, chunk);
                    IguanaParser parser = new IguanaParser(grammar);
                    long t1 = System.currentTimeMillis();
                    List<Pair> parseResults = parser.getReachabilities(input,
                                new ParseOptions.Builder().setAmbiguous(false).build());
                    long t2 = System.currentTimeMillis();
                    long curT = t2 - t1;
                    System.out.println("time:" + curT);
                    ((Neo4jBenchmarkInput) input).close();
                    if (iter >= warmUp && parseResults != null) {
                        vertexToTime.putIfAbsent(sz.toString() + iter, new ArrayList<>());
                        vertexToTime.get(sz.toString() + iter).add((int) curT);
                    }
//                    if (iter >= warmUp && parseResults != null && chunk.contains(1122)) {
//                        outStatsTime.print(chunk.toString());
//                        vertexToTime.get(chunk.toString() + iter).forEach(x -> outStatsTime.print("," + x));
//                        outStatsTime.println();
//                    }
                }
                if (iter >= warmUp) {
                    outStatsTime.print(sz);
                    vertexToTime.get(sz.toString() + iter).forEach(x -> outStatsTime.print("," + x));
                    outStatsTime.println();
                }
            }
        }

        outStatsTime.close();
    }

    public static void benchmark(String relType, int rightNode, int warmUp, int maxIter, String pathToGrammar, String dataset) throws FileNotFoundException {
        BiFunction<Relationship, Direction, String> f = getFunction(relType);
        Map<String, List<Integer>> vertexToTime = new HashMap<>();

        Grammar grammar;
        try {
            grammar = Grammar.load(pathToGrammar, "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        PrintWriter outStatsTime = new PrintWriter("results/" + dataset + "_time_" + relType + ".csv");
        outStatsTime.append("chunk_size, time");
        outStatsTime.append("\n");
        List<Integer> chunkSize = Arrays.asList(1, 2, 4, 8, 16, 32, 50, 100, 500, 1000, 5000, 10000, rightNode);
        List<Integer> vertices = Interval.zeroTo(rightNode - 1);
        for (Integer sz : chunkSize) {
            List<List<Integer>> chunks = Lists.partition(vertices, sz);
            for (Integer iter = 0; iter < maxIter; iter++) {
                for (List<Integer> chunk : chunks) {
                    System.out.println("iter " + iter + " chunkSize " + sz);

                    GraphInput input = new Neo4jBenchmarkInput(graphDb, f, chunk);
                    IguanaParser parser = new IguanaParser(grammar);
                    long t1 = System.currentTimeMillis();
                    Map<Pair, ParseTreeNode> parseTreeNodes = parser.getParserTree(input,
                            new ParseOptions.Builder().setAmbiguous(true).build());
                    long t2 = System.currentTimeMillis();
                    long curT = t2 - t1;
                    ((Neo4jBenchmarkInput) input).close();
                    if (iter >= warmUp && parseTreeNodes != null) {

                        vertexToTime.putIfAbsent(sz.toString() + iter, new ArrayList<>());
                        vertexToTime.get(sz.toString() + iter).add((int) curT);
                    }
                }
                if (iter >= warmUp) {
                    outStatsTime.print(sz);
                    vertexToTime.get(sz.toString() + iter).forEach(x -> outStatsTime.print("," + x));
                    outStatsTime.println();
                }
            }
        }
        outStatsTime.close();
    }


    private static int countNumberOfPaths(Map<Pair, ParseTreeNode> parseTreeNodes, Map<Integer, Integer> counter) {
        int res = 0;
        Map<ParseTreeNode, Integer> nodeToCurPaths = new HashMap<>();
        Map<ParseTreeNode, List<Integer>> nodeToLength = new HashMap<>();

        for (Pair verticesPair : parseTreeNodes.keySet()) {
            ParseTreeNode parseTreeNode = parseTreeNodes.get(verticesPair);
            traverse(parseTreeNode, nodeToCurPaths, nodeToLength);
            res += nodeToCurPaths.get(parseTreeNode);

            nodeToLength.get(parseTreeNode).forEach(length -> {
                counter.putIfAbsent(length, 0);
                counter.put(length, counter.get(length) + 1);
            });
        }
        return res;
    }

    private static void traverse(ParseTreeNode parseTreeNode, Map<ParseTreeNode, Integer> nodeToCurPaths, Map<ParseTreeNode, List<Integer>> nodeToLengths) {
        if (nodeToCurPaths.containsKey(parseTreeNode)) {
            return;
        }

        List<ParseTreeNode> children = parseTreeNode.children();
        int result;

        List<Integer> lengths = new ArrayList<>();
        if (parseTreeNode instanceof DefaultTerminalNode) {
            result = 1;
            lengths.add(1);
        } else if (parseTreeNode instanceof NonterminalNode) {
            int curRes = 1;
            lengths.add(0);
            List<Integer> temp = new ArrayList<>();
            for (ParseTreeNode childNode : children) {
                traverse(childNode, nodeToCurPaths, nodeToLengths);
                curRes *= nodeToCurPaths.get(childNode);
                temp.clear();
                nodeToLengths.get(childNode)
                        .forEach(newL -> lengths.forEach(prevL -> temp.add(newL + prevL)));
                lengths.clear();
                lengths.addAll(temp);
            }
            result = curRes;
        } else if (parseTreeNode instanceof AmbiguityNode) {
            int curRes = 0;
            for (ParseTreeNode childNode : children) {
                traverse(childNode, nodeToCurPaths, nodeToLengths);
                curRes += nodeToCurPaths.get(childNode);
                lengths.addAll(nodeToLengths.get(childNode));
            }
            result = curRes;
        } else {
            throw new RuntimeException("Unexpected type of node");
        }
        nodeToCurPaths.put(parseTreeNode, result);
        nodeToLengths.put(parseTreeNode, lengths);
    }

    private static void removeData() {
        try (Transaction tx = graphDb.beginTx()) {
            tx.getAllNodes().forEach(node -> {
                node.getRelationships().forEach(Relationship::delete);
                node.delete();
            });
            tx.commit();
        }
    }
}
