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
//}
//
