package org.iguana.util.visualization;

import org.iguana.parsetree.*;

import java.util.HashMap;
import java.util.Map;

import static iguana.utils.visualization.GraphVizUtil.*;

public class ParseTreeToDot {

    private StringBuilder sb = new StringBuilder();

    private Map<Object, Integer> ids = new HashMap<>();

    public String toDot(ParseTreeNode node) {
        node.accept(new ToDotParseTreeVisitor());
        return sb.toString();
    }

    class ToDotParseTreeVisitor implements ParseTreeVisitor<Void> {
        @Override
        public Void visit(NonterminalNode node) {
            String label = String.format("(%s, %d, %d)", node.definition(), node.start(), node.end());

            sb.append("\"" + getId(node) + "\"" + String.format(ROUNDED_RECTANGLE, "black", replaceWhiteSpace(label)) + "\n");
            addEdgesToChildren(node);

            visitChildren(node);
            return null;
        }

        @Override
        public Void visit(AmbiguityNode node) {
            sb.append("\"" + getId(node) + "\"" + String.format(DIAMOND, "red", ""));
            addEdgesToChildren(node);
            visitChildren(node);
            return null;
        }

        @Override
        public Void visit(TerminalNode node) {
            String color = "black";
            String label = String.format("(%s, %d, %d): \"%s\"", node.definition().getName(), node.start(), node.end(), node.text());
            sb.append("\"" + getId(node) + "\"" + String.format(ROUNDED_RECTANGLE, color, replaceWhiteSpace(label)) + "\n");
            return null;
        }

        private int getId(Object node) {
            return ids.computeIfAbsent(node, k -> ids.size() + 1);
        }

        private String replaceWhiteSpace(String s) {
            return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
        }

        private void addEdgesToChildren(ParseTreeNode node) {
            for (ParseTreeNode child : node.children()) {
                addEdgeToChild(getId(node), getId(child));
            }
        }

        private void addEdgeToChild(int parentNodeId, int childNodeId) {
            sb.append(EDGE + "\"" + parentNodeId + "\"" + "->" + "{\"" + childNodeId + "\"}" + "\n");
        }

        public void visitChildren(ParseTreeNode node) {
            for (ParseTreeNode child : node.children()) {
                child.accept(this);
            }
        }
    }

}
