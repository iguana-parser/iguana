//package experiments.neo4j;
//
///*
// * Licensed to Neo4j under one or more contributor
// * license agreements. See the NOTICE file distributed with
// * this work for additional information regarding copyright
// * ownership. Neo4j licenses this file to you under
// * the Apache License, Version 2.0 (the "License"); you may
// * not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied. See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//
//import org.neo4j.configuration.GraphDatabaseSettings;
//import org.neo4j.dbms.api.DatabaseManagementService;
//import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.RelationshipType;
//import org.neo4j.graphdb.Transaction;
//
//import java.io.File;
//import java.io.IOException;
//
//import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
//
//public class DatabaseManager {
////    private static final File databaseDirectory = new File("target/neo4j-hello-db");
//    private static final File databaseDirectory = new File("/Users/annavlasova/Downloads/neo4j-enterprise-4.0.4_2");
//    private GraphDatabaseService graphDb;
//    private DatabaseManagementService managementService;
//
//    private enum RelTypes implements RelationshipType {
//        KNOWS
//    }
//
//    public static void main(final String[] args) throws IOException {
//        DatabaseManager hello = new DatabaseManager();
//        hello.createDb();
////        hello.removeData();
////        Thread.sleep(10000);
////        hello.shutDown();
//    }
//
//    void createDb() throws IOException {
//        // start db
//        managementService = new DatabaseManagementServiceBuilder(databaseDirectory)
//                .setConfig( GraphDatabaseSettings.read_only, true )
//                .build();
//
//        graphDb = managementService.database( DEFAULT_DATABASE_NAME );
//
//        try (Transaction tx = graphDb.beginTx()) {
//            System.out.println(tx.getAllNodes().stream().count());
//        }
////        registerShutdownHook(managementService);
////
////        // transaction
////        try (Transaction tx = graphDb.beginTx()) {
////            // Database operations go here
////            // add data
////            Node node1 = tx.createNode();
////            node1.setProperty("node label", "node1");
////            Node node2 = tx.createNode();
////            node2.setProperty("node label", "node2");
////            Node node3 = tx.createNode();
////            node3.setProperty("node label", "node3");
////
////            node1.createRelationshipTo(node2, RelTypes.KNOWS).setProperty("tag", "a");
////            node1.createRelationshipTo(node3, RelTypes.KNOWS).setProperty("tag", "b");
////
////            // read data
////            node1.getRelationships().forEach(r -> {
////                System.out.println((String)r.getStartNode().getProperty("node label") + " "
////                        + (String)r.getEndNode().getProperty("node label") + " "
////                        + (String)r.getProperty("tag"));
////            });
////            tx.commit();
////        }
//    }
//
//    void removeData() {
//        try (Transaction tx = graphDb.beginTx()) {
//            // remove the data
////            tx.getAllNodes().forEach(node -> {
////                node.getRelationships().forEach(Relationship::delete);
////                node.delete();
////            });
////            tx.commit();
//        }
//    }
//
////    void shutDown() {
////        System.out.println();
////        System.out.println("Shutting down database ...");
////        // shutdown server
////        managementService.shutdown();
////    }
//
////    private static void registerShutdownHook(final DatabaseManagementService managementService) {
////        // Registers a shutdown hook for the Neo4j instance so that it
////        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
////        // running application).
////        Runtime.getRuntime().addShutdownHook(new Thread() {
////            @Override
////            public void run() {
////                managementService.shutdown();
////            }
////        });
////    }
//}