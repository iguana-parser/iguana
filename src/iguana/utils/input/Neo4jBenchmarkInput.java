package iguana.utils.input;

import org.eclipse.collections.impl.list.Interval;
import org.neo4j.graphdb.*;

import java.io.Closeable;
import java.util.*;
import java.util.function.BiFunction;


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
        this.finalVertices = Interval.zeroTo(48815);
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
        return result;
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
