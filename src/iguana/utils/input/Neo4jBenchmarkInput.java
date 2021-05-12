package iguana.utils.input;

import org.eclipse.collections.impl.list.Interval;
import org.neo4j.graphdb.*;
import org.neo4j.internal.kernel.api.NodeCursor;
import org.neo4j.kernel.api.KernelTransaction;
import org.neo4j.kernel.impl.coreapi.TransactionImpl;
import java.io.Closeable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Neo4jBenchmarkInput extends Neo4jGraphInput implements Closeable
{
    private final GraphDatabaseService graphDb;
    private final BiFunction<Relationship, Direction, String> toLabel;
    private final List<Integer> startVertices;
    Transaction tx;

    public Neo4jBenchmarkInput(GraphDatabaseService graphDb, BiFunction<Relationship, Direction, String> toLabel, List<Integer> startVertices) {
        super(graphDb);
        this.graphDb = graphDb;
        this.toLabel = toLabel;
        this.startVertices = startVertices;
        this.tx = graphDb.beginTx();
    }

    public long nVertices() {
        try (Transaction tx = graphDb.beginTx()) {
            return tx.getAllNodes().stream().count();
        }
    }

    @Override
    public List<Integer> nextSymbols(int index) {
        List<Integer> result = new ArrayList<>();

        if (isFinal(index)) {
            result.add(EOF);
        }
        Iterable<Relationship> relationships = tx.getNodeById(index).getRelationships();
        for (Relationship rel : relationships) {
            String tmp = toLabel.apply(
                    rel,
                    rel.getStartNodeId() == index ? Direction.OUTGOING : Direction.INCOMING
            );
            if (tmp != null) {
                result.add((int) tmp.charAt(0));
            }
        }
//        tx.getNodeById(index).getRelationships()
//                .forEach(rel -> {
//                    String tmp = toLabel.apply(
//                            rel,
//                            rel.getStartNodeId() == index ? Direction.OUTGOING : Direction.INCOMING
//                    );
//                    if (tmp != null) {
//                        result.add((int) tmp.charAt(0));
//                    }
//                });

        return result;
    }

    @Override
    public boolean isFinal(int index) {
        return true;
    }

//    @Override
//    public List<Integer> getStartVertices() {
//        List<Integer> result = new ArrayList<>();
//        ResourceIterable<Node> nodes = tx.getAllNodes();
//        for (Node node: nodes) {
//            long id = node.getId();
//            if (id == start) {
//                result.add((int) id);
//            }
//        }
//        return result;
////        return tx.getAllNodes().stream()
////                .filter(node -> node.getId() == start)
////                .map(node -> (int) node.getId())
////                .collect(Collectors.toList());
//    }
    @Override
    public List<Integer> getStartVertices() {
    return this.startVertices;
}


    @Override
    public List<Integer> getFinalVertices() {
        return Interval.zeroTo(15399);
//        TransactionImpl tx = graphDb.beginTx();
//        KernelTransaction ktx = tx.;
//        final NodeCursor cursor = ktx.cursors().allocateNodeCursor();
//        ktx.dataRead().allNodesScan(cursor);
////        return new PrefetchingResourceIterator<Node>() {
////            protected Node fetchNextOrNull() {
////                if (cursor.next()) {
////                    return TransactionImpl.newNodeEntity(cursor.nodeReference());
////                } else {
////                    this.close();
////                    return null;
////                }
////            }
//        List<Integer> res = new ArrayList<>();
//        while (cursor.next()) {
//            res.add(((Long) cursor.nodeReference()).intValue());
//        }
////
////        Result result = tx.execute("MATCH (n) RETURN ID(n)");
////        tx.getAllNodes().forEach(node -> {
////            result.add((int) node.getId());
////        });
////        while ( result.hasNext() )
////        {
////            Map<String,Object> row = result.next();
////            for ( Map.Entry<String,Object> column : row.entrySet() )
////            {
////                res.add(((Long) column.getValue()).intValue());
////            }
////        }
//        return res;
//        return tx.getAllNodes().stream()
//                .map(node -> (int) node.getId())
//                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getDestVertex(int v, String t) {
        List<Integer> result = new ArrayList<>();
        Iterable<Relationship> relationships = tx.getNodeById(v).getRelationships();
        for (Relationship rel : relationships) {
            final Direction direction = rel.getStartNodeId() == v
                    ? Direction.OUTGOING
                    : Direction.INCOMING;

            String tmp = toLabel.apply(rel, direction);
            if (tmp != null && tmp.equals(t)) {
                result.add(direction == Direction.INCOMING
                        ? (int) rel.getStartNode().getId()
                        : (int) rel.getEndNode().getId());
            }
        }
        return result;
    }
//        return StreamSupport.stream(tx.getNodeById(v).getRelationships().spliterator(), false)
//                .map(rel -> {
//                    final Direction direction = rel.getStartNodeId() == v
//                            ? Direction.OUTGOING
//                            : Direction.INCOMING;
//
//                    String tmp = toLabel.apply(rel, direction);
//                    if (tmp != null && tmp.equals(t)) {
//                        return direction == Direction.INCOMING
//                                ? (int) rel.getStartNode().getId()
//                                : (int) rel.getEndNode().getId();
//                    }
//                    return null;
//                }).filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }

    @Override
    public void close() {
        tx.close();
    }
}
