package iguana.utils.input;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.io.Closeable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Neo4jBenchmarkInput extends Neo4jGraphInput implements Closeable {
    private GraphDatabaseService graphDb;
    private BiFunction<Relationship, Direction, String> toLabel;
    private int start;
    Transaction tx;

    public Neo4jBenchmarkInput(GraphDatabaseService graphDb, BiFunction<Relationship, Direction, String> toLabel, int start) {
        super(graphDb);
        this.graphDb = graphDb;
        this.toLabel = toLabel;
        this.start = start;
        this.tx = graphDb.beginTx();
    }

    public long nVertices() {
        try (Transaction tx = graphDb.beginTx()) {
            return tx.getAllNodes().stream().count();
        }
    }

    @Override
    public Stream<Integer> nextSymbols(int index) {
        Stream<Integer> nextSymbols = StreamSupport.stream(tx.getNodeById(index).getRelationships().spliterator(), false)
                .map(rel -> {
                    final Direction direction = rel.getStartNodeId() == index
                            ? Direction.OUTGOING
                            : Direction.INCOMING;
                    String tmp = toLabel.apply(rel, direction);
                    if (tmp != null) {
                        return (int) tmp.charAt(0);
                    }
                    return null;
                })
                .filter(Objects::nonNull);

        if (isFinal(index)) {
            return Stream.concat(Stream.of(EOF), nextSymbols);
        }
        return nextSymbols;
    }

    @Override
    public boolean isFinal(int index) {
        return true;
    }

    @Override
    public List<Integer> getStartVertices() {
        return tx.getAllNodes().stream()
                .filter(node -> node.getId() == start)
                .map(node -> (int) node.getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getFinalVertices() {
        return tx.getAllNodes().stream()
                .map(node -> (int) node.getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getDestVertex(int v, String t) {
        return StreamSupport.stream(tx.getNodeById(v).getRelationships().spliterator(), false)
                .map(rel -> {
                    final Direction direction = rel.getStartNodeId() == v
                            ? Direction.OUTGOING
                            : Direction.INCOMING;

                    String tmp = toLabel.apply(rel, direction);
                    if (tmp != null && tmp.equals(t)) {
                        return direction == Direction.INCOMING
                                ? (int) rel.getStartNode().getId()
                                : (int) rel.getEndNode().getId();
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void close() {
        tx.close();
    }
}
