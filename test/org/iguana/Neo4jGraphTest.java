package org.iguana;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jBenchmarkInput;
import iguana.utils.input.Neo4jGraphInput;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.*;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.cypher.internal.v4_0.expressions.Null;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
//import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//import org.neo4j.graphdb.factory.GraphDatabaseSettings;
//import org.neo4j.kernel.configuration.BoltConnector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static benchmark.Neo4jBenchmark.getFunction;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

public class Neo4jGraphTest {
    private static final File databaseDirectory = new File("target/neo4j-hello-db");
    private static GraphDatabaseService graphDb;
    private static DatabaseManagementService managementService;

    @BeforeClass
    public static void initDb() throws IOException {
        org.neo4j.io.fs.FileUtils.deleteRecursively(databaseDirectory);

        managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
                .setConfig(BoltConnector.enabled, true)
                .setConfig(BoltConnector.listen_address, new SocketAddress("localhost", 7687))
                .build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
//        GraphDatabaseService graphDb = new GraphDatabaseFactory()
//                .newEmbeddedDatabaseBuilder( databaseDirectory )
//                .loadPropertiesFromFile( "neo4j.conf" )
//                .newGraphDatabase();
//        registerShutdownHook( graphDb );
    }

//    private static void registerShutdownHook( final GraphDatabaseService graphDb )
//    {
//        // Registers a shutdown hook for the Neo4j instance so that it
//        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
//        // running application).
//        Runtime.getRuntime().addShutdownHook( new Thread()
//        {
//            @Override
//            public void run()
//            {
//                graphDb.shutdown();
//            }
//        } );
//    }

    @AfterClass
    public static void finalizeDb() {
        removeData();
//        graphDb.shutdown();
        managementService.shutdown();
    }

    @Before
    public void clearDb() {
        removeData();
    }

    @Test
    public void testNeo4jGraphInput() {
        try (Transaction tx = graphDb.beginTx()) {
            Node node1 = tx.createNode();
            node1.addLabel(Label.label("start"));

            Node node2 = tx.createNode();
            node2.addLabel(Label.label("final"));

            node1.createRelationshipTo(node2, RelationshipType.withName("simple_edge")).setProperty("tag", "a");
            node2.createRelationshipTo(node2, RelationshipType.withName("simple_edge")).setProperty("tag", "a");
            tx.commit();
        }

        GraphInput input = new Neo4jGraphInput(graphDb);
        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test2/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
    }

    @Test
    public void testNeo4jEnzymeInput() throws FileNotFoundException {
        String fileName = "enzyme_broaderTransitive.txt";
        Map<Integer, Long> numPaths = new HashMap<>();
        HashMap<Integer, Long > nodeList = new HashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

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

//                    System.out.println("from: " + splited[0] + " id: " + node1.getId() + " label: " + splited[1] + "  to: " + splited[2] + "id: " + node2.getId());
                });
//                Iterable<Label> labels = tx.getAllLabels();
//                for (Label label: labels) {
//                    System.out.println("label: " + label);
//                    tx.findNodes(label).stream().forEach(System.out::println);
//                }
//                Iterable<RelationshipType> relationships = tx.getAllRelationshipTypes();
//                for (RelationshipType reltype : relationships) {
//                System.out.println(reltype);
//                }
                tx.commit();
//                tx.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("nodelist size: " + nodeList.size());
        BiFunction<Relationship, Direction, String> f = getFunction("bt");
        for (int start = 109; start <= 109; start += 1) {
            GraphInput input = new Neo4jBenchmarkInput(graphDb, f, start);
//            System.out.println(start + " nextS: " + input.nextSymbols(start));
            Grammar grammar;
            try {
                grammar = Grammar.load("test/resources/grammars/graph/Test2/grammar.json", "json");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("No grammar.json file is present");
            }
//            System.out.println("nextsymbols from start: " + input.nextSymbols(start));
            IguanaParser parser = new IguanaParser(grammar);
//      GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);
            long t1 = System.currentTimeMillis();
            Map<Pair, ParseTreeNode> parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
            long t2 = System.currentTimeMillis();
            long curT = t2 - t1;
            if (parseTreeNode != null) {
                numPaths.put(start, curT);
                System.out.println(curT);
                System.out.println(start + " " + parseTreeNode.size());
            }
//            ((Neo4jBenchmarkInput) input).close();
        }
        PrintWriter out = new PrintWriter("results/" + "enzyme_time_num_bt_inmem" + ".csv");
        numPaths.keySet().forEach(vertex -> {
            out.println(vertex + "," + numPaths.get(vertex));
        });
        out.close();
    }


    @Test
    public void testNeo4jGraphInput2() {
        try (Transaction tx = graphDb.beginTx()) {
            Node node1 = tx.createNode();
            node1.addLabel(Label.label("start"));

            node1.addLabel(Label.label("final"));

            node1.createRelationshipTo(node1, RelationshipType.withName("simple_edge")).setProperty("tag", "a");
            tx.commit();
        }

        GraphInput input = new Neo4jGraphInput(graphDb);
        Grammar grammar;
        try {
            grammar = Grammar.load("test/resources/grammars/graph/Test2/grammar.json", "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        IguanaParser parser = new IguanaParser(grammar);
        GrammarGraph grammarGraph = GrammarGraphBuilder.from(grammar);

        Map<Pair, ParseTreeNode> parseTreeNode = parser.getParserTree(input, new ParseOptions.Builder().setAmbiguous(true).build());
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
