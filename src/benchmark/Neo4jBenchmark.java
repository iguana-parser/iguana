package benchmark;

import apoc.ApocSettings;
import apoc.create.Create;
import apoc.help.Help;
import apoc.load.*;
import apoc.periodic.*;
import static java.util.Arrays.asList;
import com.google.common.collect.Lists;

import iguana.utils.input.GraphInput;
import iguana.utils.input.Neo4jBenchmarkInput;
import org.eclipse.collections.impl.list.Interval;
import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;
import org.iguana.parser.Pair;
import org.iguana.parser.ParseOptions;
import org.iguana.parsetree.ParseTreeNode;

import org.iguana.util.Tuple;
import org.neo4j.configuration.GraphDatabaseSettings;
import org.neo4j.configuration.connectors.BoltConnector;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.exceptions.KernelException;
import org.neo4j.graphdb.*;
import org.neo4j.io.fs.FileUtils;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;
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
    //    args1 nodeNumber
    //    args2 number of warm up iteration
    //    args3 total number of iterations
    //    args4 path to dataset
    //    args5 path to grammar
    //    args6 dataset name = name of file with results
    //    args7 grammar name (g1/g2/geo)
    public static void main(String[] args) throws IOException {

        loadGraph(args[6], Integer.parseInt(args[1]), args[4], args[0]);
//        benchmark(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[5], args[6]);
        benchmarkReachabilities(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[5], args[6], args[7]);
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
                if (direction.equals(Direction.OUTGOING)) {
                    return "a";
                } else if (direction.equals(Direction.INCOMING)) {
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
                if (direction.equals(Direction.OUTGOING)) {
                    return "a";
                } else if (direction.equals(Direction.INCOMING)) {
                    return "b";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            } else if (rel.isType(RelationshipType.withName("type"))) {
                if (direction.equals(Direction.OUTGOING)) {
                    return "c";
                } else if (direction.equals(Direction.INCOMING)) {
                    return "d";
                } else {
                    throw new RuntimeException("Unexpected direction");
                }
            }
            return null;
        };
    }


    public static void registerProcedure(GraphDatabaseService graphDb, List<Class<?>> procedures) {
        GlobalProcedures globalProcedures = ((GraphDatabaseAPI) graphDb).getDependencyResolver().resolveDependency(GlobalProcedures.class);
        for (Class<?> procedure : procedures) {
            try {
                globalProcedures.registerProcedure(procedure, true);
                globalProcedures.registerFunction(procedure, true);
                globalProcedures.registerAggregationFunction(procedure, true);
            } catch (KernelException e) {
                throw new RuntimeException("while registering " + procedure, e);
            }
        }
    }

    public static void loadGraph(String dataset,  int nodeNumber, String pathToDataset, String typeOfRelationships) throws IOException {
        FileUtils.deleteRecursively(databaseDirectory);

        managementService =
                new DatabaseManagementServiceBuilder(databaseDirectory)
                        .setConfig(GraphDatabaseSettings.pagecache_memory, "100G")
                        .setConfig(GraphDatabaseSettings.tx_state_max_off_heap_memory, java.lang.Long.parseLong("24000000000"))
                        .setConfig(GraphDatabaseSettings.pagecache_warmup_enabled, true)
                        .setConfig(GraphDatabaseSettings.procedure_whitelist, List.of("gds.*","apoc.*", "apoc.load.*"))
                        .setConfig(GraphDatabaseSettings.procedure_unrestricted, List.of("gds.*", "apoc.*"))
                        .setConfig(GraphDatabaseSettings.default_allowed,"gds.*,apoc.*")
                        .setConfig(BoltConnector.enabled, true)
                        .setConfig(ApocSettings.apoc_import_file_enabled, true)
                        .setConfig(ApocSettings.apoc_import_file_use__neo4j__config, false)
                        .build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);

        registerProcedure(graphDb, asList(
                Create.class,
                Help.class,
                LoadCsv.class,
                Periodic.class
        ));

        try (Transaction tx = graphDb.beginTx()) {
            for (int i = 0; i < nodeNumber; ++i) {
                String s = String.format("CREATE (:Node {name: '%d'});", i);
                tx.execute(s);
            }
            tx.commit();
        }

        try (Transaction tx = graphDb.beginTx()) {
            tx.execute("CREATE CONSTRAINT node_unique_name ON (n:Node) ASSERT n.name IS UNIQUE");
            tx.commit();
        }

        System.out.println("done");
        if ("bt".equals(typeOfRelationships)) {
            try (Transaction tx = graphDb.beginTx()) {
                tx.execute("        CALL apoc.periodic.iterate(\n" +
                           "            \"CALL apoc.load.csv('FILE:///" + pathToDataset + dataset + "_broaderTransitive.csv') YIELD map AS row RETURN row\",\n" +
                           "            \"MATCH (f:Node {name: row.from}), (t:Node {name: row.to})\n" +
                           "            CREATE (f)-[:broaderTransitive]->(t)\",\n" +
                           "            {batchSize:10000, parallel:false}\n" +
                           "        )\n" +
                           "        YIELD batches, total;\n");
                tx.execute("        CALL apoc.periodic.iterate(\n" +
                           "            \"CALL apoc.load.csv('FILE:///" + pathToDataset + dataset + "_other.csv') YIELD map AS row RETURN row\",\n" +
                           "            \"MATCH (f:Node {name: row.from}), (t:Node {name: row.to})\n" +
                           "            CREATE (f)-[:other]->(t)\",\n" +
                           "            {batchSize:100000, parallel:false}\n" +
                           "        )\n" +
                           "        YIELD batches, total;\n");
                tx.commit();
            }
        } else if ("st".equals(typeOfRelationships)){
            try (Transaction tx = graphDb.beginTx()) {
                tx.execute("        CALL apoc.periodic.iterate(\n" +
                           "            \"CALL apoc.load.csv('FILE:///" + pathToDataset + dataset + "_subClassOf.csv') YIELD map AS row RETURN row\",\n" +
                           "            \"MATCH (f:Node {name: row.from}), (t:Node {name: row.to})\n" +
                           "            CREATE (f)-[:subClassOf]->(t)\",\n" +
                           "            {batchSize:10000, parallel:false}\n" +
                           "        )\n" +
                           "        YIELD batches, total;\n");
                tx.execute("        CALL apoc.periodic.iterate(\n" +
                           "            \"CALL apoc.load.csv('FILE:///" + pathToDataset + dataset + "_type.csv') YIELD map AS row RETURN row\",\n" +
                           "            \"MATCH (f:Node {name: row.from}), (t:Node {name: row.to})\n" +
                           "            CREATE (f)-[:type]->(t)\",\n" +
                           "            {batchSize:10000, parallel:false}\n" +
                           "        )\n" +
                           "        YIELD batches, total;\n");
                tx.execute("        CALL apoc.periodic.iterate(\n" +
                           "            \"CALL apoc.load.csv('FILE:///" + pathToDataset + dataset + "_other.csv') YIELD map AS row RETURN row\",\n" +
                           "            \"MATCH (f:Node {name: row.from}), (t:Node {name: row.to})\n" +
                           "            CREATE (f)-[:other]->(t)\",\n" +
                           "            {batchSize:100000, parallel:false}\n" +
                           "        )\n" +
                           "        YIELD batches, total;\n");
                tx.commit();
            }
        }
    }

    public static void benchmarkReachabilities(String relType, int nodeNumber, int warmUp, int maxIter, String pathToGrammar, String dataset, String grammarName) throws FileNotFoundException {
        BiFunction<Relationship, Direction, String> f = getFunction(relType);

        Map<String, List<Integer>> vertexToTime = new HashMap<>();
        Grammar grammar;
        try {
            grammar = Grammar.load(pathToGrammar, "json");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No grammar.json file is present");
        }
        PrintWriter resultime = new PrintWriter("results/" + dataset + "_" + grammarName + "_reachabilities.txt");

        List<Integer> vertices = Interval.zeroTo(nodeNumber - 1);
        List<java.lang.Long> resTime = new ArrayList<>(maxIter - warmUp);
        //List<List<Integer>> verticesPartitioned = Lists.partition(vertices, nodeNumber);
        for (int iter = 0; iter < maxIter; ++iter) {
            IguanaParser parser = new IguanaParser(grammar);
            GraphInput input = new Neo4jBenchmarkInput(graphDb, f, vertices.stream(), nodeNumber);
            long resAns = 0;
            long t1 = System.nanoTime();
            //long t1_local = System.nanoTime();
            Stream<Pair> parseResults = parser.getReachabilities(input,
                    new ParseOptions.Builder().setAmbiguous(false).build());
            //long t2_local = System.nanoTime();
           // long stepTime = t2_local - t1_local;

            if (parseResults != null) {
                resAns = parseResults.count();
            }
            long t2 = System.nanoTime();

            //System.out.println("Step time is " + stepTime);
            ((Neo4jBenchmarkInput) input).close();

            if (iter >= warmUp) {
                resTime.add(t2 - t1);
            }
            System.out.println("Total answer: " + resAns);
            System.out.println("Total time: " + (t2 - t1));
        }
        resTime.forEach(resultime::println);
        resultime.close();
    }

    public static void benchmark(String relType, int nodeNumber, int warmUp, int maxIter, String pathToGrammar, String dataset) throws FileNotFoundException {
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
        List<Integer> chunkSize = Arrays.asList(1, 2, 4, 8, 16, 32, 50, 100, 500, 1000, 5000, 10000, nodeNumber);
        List<Integer> vertices = Interval.zeroTo(nodeNumber - 1);
        for (Integer sz : chunkSize) {
            List<List<Integer>> chunks = Lists.partition(vertices, sz);
            for (int iter = 0; iter < maxIter; iter++) {
                for (List<Integer> chunk : chunks) {
                    System.out.println("iter " + iter + " chunkSize " + sz);

                    GraphInput input = new Neo4jBenchmarkInput(graphDb, f, chunk.stream(), nodeNumber);
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
