package org.iguana.util.visualization;

import org.iguana.parsetree.AmbiguityNode;
import org.iguana.parsetree.ErrorNode;
import org.iguana.parsetree.MetaSymbolNode;
import org.iguana.parsetree.NonterminalNode;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.parsetree.ParseTreeVisitor;
import org.iguana.parsetree.TerminalNode;
import org.iguana.utils.input.Input;
import org.iguana.utils.visualization.DotGraph;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.iguana.utils.visualization.DotGraph.newEdge;
import static org.iguana.utils.visualization.DotGraph.newNode;

public class ParseTreeToDot implements ParseTreeVisitor<Integer> {

    private final DotGraph dotGraph;
    private final Input input;
    private final Set<String> exclude;

    public static DotGraph getDotGraph(ParseTreeNode node, Input input) {
        return getDotGraph(node, input, Collections.emptySet());
    }

    public static DotGraph getDotGraph(ParseTreeNode node, Input input, Set<String> exclude) {
        DotGraph dotGraph = new DotGraph();
        node.accept(new ParseTreeToDot(dotGraph, input, exclude));
        return dotGraph;
    }

    private ParseTreeToDot(DotGraph dotGraph, Input input, Set<String> exclude) {
        this.dotGraph = dotGraph;
        this.input = input;
        this.exclude = exclude;
    }

    @Override
    public Integer visitNonterminalNode(NonterminalNode node) {
        int id = nextId();
        String label = String.format("(%s, %d, %d)", node.getGrammarDefinition().getHead().getName(), node.getStart(),
            node.getEnd());
        dotGraph.addNode(newNode(id, label));

        visitChildren(node, id);
        return id;
    }

    @Override
    public List<Integer> visitStarNode(MetaSymbolNode.StarNode node) {
        return visitMetaSymbolNode(node);
    }

    @Override
    public List<Integer> visitPlusNode(MetaSymbolNode.PlusNode node) {
        return visitMetaSymbolNode(node);
    }

    @Override
    public Optional<Integer> visitOptionNode(MetaSymbolNode.OptionNode node) {
        return Optional.of(visitMetaSymbolNode(node).get(0));
    }

    @Override
    public List<Integer> visitStartNode(MetaSymbolNode.StartNode node) {
        return visitMetaSymbolNode(node);
    }

    @Override
    public Integer visitAltNode(MetaSymbolNode.AltNode node) {
        return visitMetaSymbolNode(node).get(0);
    }

    @Override
    public List<Integer> visitGroupNode(MetaSymbolNode.GroupNode node) {
        return visitMetaSymbolNode(node);
    }

    private List<Integer> visitMetaSymbolNode(MetaSymbolNode node) {
        int id = nextId();
        String label = String.format("%s", node.getName());
        dotGraph.addNode(newNode(id, label).setShape(DotGraph.Shape.RECTANGLE));

        visitChildren(node, id);
        return Collections.singletonList(id);
    }

    @Override
    public List<Integer> visitAmbiguityNode(AmbiguityNode node) {
        int id = nextId();
        dotGraph.addNode(newNode(id).setShape(DotGraph.Shape.DIAMOND).setColor(DotGraph.Color.RED));

        visitChildren(node, id);
        return Collections.singletonList(id);
    }

    @Override
    public Integer visitTerminalNode(TerminalNode node) {
        if (exclude.contains(node.getGrammarDefinition().getName())) return null;

        String text = input.subString(node.getStart(), node.getEnd());
        String label = String.format("(%s, %d, %d): \"%s\"", node.getGrammarDefinition().getName(), node.getStart(),
            node.getEnd(), text);
        int id = nextId();
        dotGraph.addNode(newNode(id, label).setShape(DotGraph.Shape.ROUNDED_RECTANGLE));
        return id;
    }

    @Override
    public Integer visitErrorNode(ErrorNode node) {
        String text = input.subString(node.getStart(), node.getEnd());
        String label = String.format("(Error, %d, %d): \"%s\"", node.getStart(), node.getEnd(), text);
        int id = nextId();
        dotGraph.addNode(newNode(id, label).setShape(DotGraph.Shape.ROUNDED_RECTANGLE));
        return id;
    }

    private int id = 0;

    private int nextId() {
        return id++;
    }

    private void addEdgeToChild(int parentNodeId, int childNodeId) {
        dotGraph.addEdge(newEdge(parentNodeId, childNodeId));
    }

    private void visitChildren(ParseTreeNode node, int nodeId) {
        for (ParseTreeNode child : node.children()) {
            if (child != null) {
                Object childId = child.accept(this);
                if (childId != null) {
                    if (childId instanceof List<?>) {
                        addEdgeToChild(nodeId, ((List<Integer>) childId).get(0));
                    } else if (childId instanceof Optional<?>) {
                        addEdgeToChild(nodeId, ((Optional<Integer>) childId).get());
                    } else {
                        addEdgeToChild(nodeId, (Integer) childId);
                    }
                }
            }
        }
    }
}
