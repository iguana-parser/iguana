package iguana.utils.input;

import org.eclipse.collections.impl.list.Interval;
import org.neo4j.graphdb.*;

import java.io.Closeable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Neo4jBenchmarkInput extends Neo4jGraphInput implements Closeable {
    private final GraphDatabaseService graphDb;
    private final BiFunction<Relationship, Direction, String> toLabel;
    private final List<Integer> startVertices;
    private final List<Integer> finalVertices;
    Transaction tx;

    public Neo4jBenchmarkInput(GraphDatabaseService graphDb, BiFunction<Relationship, Direction, String> toLabel, List<Integer> startVertices, Integer verticesNumber) {
        super(graphDb);
        this.graphDb = graphDb;
        this.toLabel = toLabel;
        this.startVertices = startVertices;
        this.tx = graphDb.beginTx();
        this.finalVertices = Interval.zeroTo(verticesNumber);
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
                });

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
        return this.startVertices;
    }

    @Override
    public List<Integer> getFinalVertices() {
        return this.finalVertices;
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

    @Override
    public void close() {
        tx.close();
    }
}
