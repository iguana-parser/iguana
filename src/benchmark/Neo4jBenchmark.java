package benchmark;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jBenchmarkInput;
import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.AmbiguityNode;
import org.iguana.parsetree.DefaultTerminalNode;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class Neo4jBenchmark {
//    private static final File databaseDirectory = new File("/Users/annavlasova/Downloads/neo4j-enterprise-4.0.4_2");
    private static GraphDatabaseService graphDb;
//    private static String relationshipName;
    private static Map<String, String> relationshipNamesMap = Map.of(
            "nt", "skos__narrowerTransitive",
            "bt", "skos__broaderTransitive"
    );
    private static String st = "st";

//    args0 rel type (st/bt/nt)
//    args1 rightNode
//    args2 number of warm up iteration
//    args3 total number of iterations
//    args4 path to database
//    args5 path to grammar
//    args6 dataset name = name of file with results
    public static void main(String[] args) throws FileNotFoundException {
        DatabaseManagementService managementService = new DatabaseManagementServiceBuilder(new File(args[4]))
                .setConfig(GraphDatabaseSettings.read_only, true)
                .build();

        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        benchmark(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[5], args[6]);
    }

    public static BiFunction<Relationship, Direction, String> getFunction(String relationshipName) {
        if (relationshipNamesMap.containsKey(relationshipName)) {
            return singleFunction(relationshipNamesMap.get(relationshipName));
        } else if (relationshipName.equals(st)){
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
            if (rel.isType(RelationshipType.withName("rdfs__subClassOf"))) {
                if (direction.equals(Direction.INCOMING)) {
                    return "a";
                } else if (direction.equals(Direction.OUTGOING)) {
                    return "b";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            } else if (rel.isType(RelationshipType.withName("rdf__type"))) {
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

    public static void benchmark(String relType, int rightNode, int warmUp, int maxIter, String pathToGrammar, String dataset) throws FileNotFoundException {
         BiFunction<Relationship, Direction, String> f = getFunction(relType);

        Map<Integer, Integer> numPaths = new HashMap<>();
        Map<Integer, List<Integer>> vertexToTime = new HashMap<>();
//        Map<Integer, List<Integer>> vertexToMem = new HashMap<>();

        Grammar grammar;
        try {
            grammar = Grammar.load(pathToGrammar, "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
//        Runtime r = Runtime.getRuntime();

        Map<Integer, Integer> counterLength = new HashMap<>();

        PrintWriter outStatsTime = new PrintWriter("results/" + dataset + "_time_" + relType + ".csv");
//        PrintWriter outStatsMem = new PrintWriter("results/enzyme_mem_" + relType + ".csv");

        for (int iter = 0; iter < maxIter; iter++) {
            for (int start = 0; start < rightNode; start += 1) {
                System.out.println("iter " + iter + " node " + start);

                GraphInput input = new Neo4jBenchmarkInput(graphDb, f, start);
//                System.out.println(input.nVertices());
                IguanaParser parser = new IguanaParser(grammar);
//                r.gc();
//                long m1 = r.totalMemory() - r.freeMemory();
                long t1 = System.currentTimeMillis();
                Map<Pair, ParseTreeNode> parseTreeNodes = parser.getParserTree(input,
                        new ParseOptions.Builder().setAmbiguous(true).build());
                long t2 = System.currentTimeMillis();
//                long m2 = r.totalMemory() - r.freeMemory();
                long curT = t2 - t1;
//                long curM = (m2 - m1);

                if (iter >= warmUp && parseTreeNodes != null) {

                    vertexToTime.putIfAbsent(start, new ArrayList<>());
                    vertexToTime.get(start).add((int) curT);
//                    vertexToMem.putIfAbsent(start, new ArrayList<>());
//                    vertexToMem.get(start).add((int) curM);
                    if (iter == maxIter - 1) {
                        numPaths.put(start, countNumberOfPaths(parseTreeNodes, counterLength));
//                        System.out.println(start + " " + countNumberOfPaths(parseTreeNodes, counterLength) + " " + counterLength);
                    }
                }
                ((Neo4jBenchmarkInput) input).close();
            }
        }
        System.out.println(counterLength);
        Map<Integer, Double> resultTime = new HashMap<>();
//        Map<Integer, Double> resultMem = new HashMap<>();

        vertexToTime.forEach((vertex, list) ->
                resultTime.put(vertex, list.stream().mapToInt(x -> x).average().getAsDouble()));

//        vertexToMem.forEach((vertex, list) ->
//                resultMem.put(vertex, list.stream().mapToInt(x -> x).average().getAsDouble() / (1024 * 1024)));

        PrintWriter out = new PrintWriter("results/" + dataset + "_" + relType + ".csv");
        numPaths.keySet().forEach(vertex -> {
            out.println(vertex + "," + numPaths.get(vertex) + "," + resultTime.get(vertex)); //+ "," + resultMem.get(vertex));
        });
        out.close();

        vertexToTime.forEach((vertex, list) -> {
            outStatsTime.print(vertex);
            list.forEach(x -> outStatsTime.print("," + x));
            outStatsTime.println();
        });
        outStatsTime.close();

//        vertexToMem.forEach((vertex, list) -> {
//            outStatsMem.print(vertex);
//            list.forEach(x -> outStatsMem.print("," + x));
//            outStatsMem.println();
//        });
//        outStatsMem.close();
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

//    private static void removeData() {
//        try (Transaction tx = graphDb.beginTx()) {
//            tx.getAllNodes().forEach(node -> {
//                node.getRelationships().forEach(Relationship::delete);
//                node.delete();
//            });
//            tx.commit();
//        }
//    }
}
