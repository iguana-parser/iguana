package iguana.utils.input;

import org.eclipse.collections.impl.list.Interval;
import org.neo4j.graphdb.*;

import java.io.Closeable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Neo4jBenchmarkInput extends Neo4jGraphInput implements Closeable {
    private final GraphDatabaseService graphDb;
    private final BiFunction<Relationship, Direction, String> toLabel;
    private final Stream<Integer> startVertices;
    private final Stream<Integer> finalVertices;
    Transaction tx;

    public Neo4jBenchmarkInput(GraphDatabaseService graphDb, BiFunction<Relationship, Direction, String> toLabel, Stream<Integer> startVertices, Integer verticesNumber) {
        super(graphDb);
        this.graphDb = graphDb;
        this.toLabel = toLabel;
        this.startVertices = startVertices;
        this.tx = graphDb.beginTx();
        this.finalVertices = IntStream.range(0, verticesNumber).boxed();
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
    public Stream<Integer> getStartVertices() {
        return this.startVertices;
    }

    @Override
    public Stream<Integer> getFinalVertices() {
        return this.finalVertices;
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
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void close() {
        tx.close();
    }
}
