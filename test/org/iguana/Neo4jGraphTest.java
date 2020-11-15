package org.iguana;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jGraphInput;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.ParseTreeNode;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.configuration.helpers.SocketAddress;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

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
    }

    @AfterClass
    public static void finalizeDb() {
        removeData();
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
