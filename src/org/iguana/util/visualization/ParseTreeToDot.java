package org.iguana.util.visualization;

import iguana.utils.input.Input;
import org.iguana.parsetree.*;

import static iguana.utils.visualization.GraphVizUtil.*;

public class ParseTreeToDot {

    private StringBuilder sb = new StringBuilder();

    private Input input;

    public String toDot(ParseTreeNode node, Input input) {
        this.input = input;
        node.accept(new ToDotParseTreeVisitor());
        return sb.toString();
    }

    class ToDotParseTreeVisitor implements ParseTreeVisitor<Integer> {

        @Override
        public Integer visit(NonterminalNode node) {
            String label = String.format("(%s, %d, %d)", node.definition(), node.start(), node.end());

            int id = nextId();
            sb.append("\"").append(id).append("\"").append(String.format(ROUNDED_RECTANGLE, "black", replaceWhiteSpace(label))).append("\n");

            visitChildren(node, id);
            return id;
        }

        @Override
        public Integer visit(MetaSymbolNode node) {
            String color = "black";
            String label = String.format("%s", node.getSymbol());

            int id = nextId();
            sb.append("\"").append(id).append("\"").append(String.format(RECTANGLE, color, replaceWhiteSpace(label))).append("\n");

            visitChildren(node, id);
            return id;
        }

        @Override
        public Integer visit(AmbiguityNode node) {
            int id = nextId();
            sb.append("\"").append(id).append("\"").append(String.format(DIAMOND, "red"));
            visitChildren(node, id);
            return id;
        }

        @Override
        public Integer visit(TerminalNode node) {
            String color = "black";
            String label = String.format("(%s, %d, %d): \"%s\"", node.definition().getName(), node.start(), node.end(), node.text(input));
            int id = nextId();
            sb.append("\"").append(id).append("\"").append(String.format(ROUNDED_RECTANGLE, color, replaceWhiteSpace(label))).append("\n");
            return id;
        }

        private int id = 0;
        private int nextId() {
            return id++;
        }

        private String replaceWhiteSpace(String s) {
            return s.replace("\\", "\\\\").replace("\t", "\\\\t").replace("\n", "\\\\n").replace("\r", "\\\\r").replace("\"", "\\\"");
        }

        private void addEdgeToChild(int parentNodeId, int childNodeId) {
            sb.append(EDGE).append("\"").append(parentNodeId).append("\" -> {\"").append(childNodeId).append("\"}").append("\n");
        }

        private void visitChildren(ParseTreeNode node, int nodeId) {
            for (ParseTreeNode child : node.children()) {
                int childId = child.accept(this);
                addEdgeToChild(nodeId, childId);
            }
        }
    }

}
