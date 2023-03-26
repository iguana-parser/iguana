package org.iguana.utils.visualization;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class DotGraph {

    private static final Node nodeInstance = new Node();
    private static final Edge edgeInstance = new Edge();

    private final StringBuilder sb = new StringBuilder();

    public DotGraph() {
        this(Direction.TO_DOWN);
    }

    public DotGraph(Direction direction) {
        sb.append("digraph sppf {").append("\n");
        sb.append("layout=dot").append("\n");
        sb.append("nodesep=.6").append("\n");
        sb.append("ranksep=.4").append("\n");
        sb.append("ordering=out").append("\n");
        sb.append("forcelabels=true").append("\n");
        if (direction == Direction.LEFT_TO_RIGHT)
            sb.append("rankdir=LR").append("\n");
    }

    public static Node newNode(int id) {
        return newNode(id, "");
    }

    public static Node newNode(int id, String label) {
        return nodeInstance.init(id, label);
    }

    public static Edge newEdge(int sourceId, int targetId) {
        return edgeInstance.init(sourceId, targetId);
    }

    public static Edge newEdge(int sourceId, int targetId, String label) {
        return edgeInstance.init(sourceId, targetId).setLabel(label);
    }

    public void addNode(Node node) {
        sb.append(node.toString());
    }

    public void addEdge(Edge edge) {
        sb.append(edge.toString());
    }

    public String getText() {
        return sb.toString() + "}";
    }

    public void generate(String fileName) throws Exception {
        Process process = new ProcessBuilder("dot", "-Tpdf", "-o", fileName).start();
        OutputStream stdin = process.getOutputStream();
        try (PrintWriter out = new PrintWriter(new BufferedOutputStream(stdin))) {
            out.write(getText());
        }

        int exitCode = process.waitFor();

        BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String line;
        while ((line = errorStream.readLine()) != null) {
            System.out.println(line);
        }

        if (exitCode != 0)
            throw new RuntimeException("");
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\\\t")
                .replace("\n", "\\\\n")
                .replace("\r", "\\\\r")
                .replace("\"", "\\\"");
    }

    public enum Direction {
        TO_DOWN,
        LEFT_TO_RIGHT
    }

    public enum Shape {
        ROUNDED_RECTANGLE,
        RECTANGLE,
        CIRCLE,
        DOUBLE_CIRCLE,
        DIAMOND
    }

    public enum Color {
        BLACK("black"),
        RED("red");

        private final String color;

        Color(String color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return color;
        }
    }

    public static class Node {

        private int id;
        private String label;
        private Shape shape;
        private Color color;
        private Color fontColor;
        private int fontSize;
        private double width;
        private double height;

        private Node init(int id, String label) {
            this.id = id;
            this.label = label;
            this.shape = Shape.ROUNDED_RECTANGLE;
            this.color = Color.BLACK;
            this.fontColor = Color.BLACK;
            this.fontSize = 10;
            this.width = 0.1;
            this.height = 0.1;
            return this;
        }

        public Node setLabel(String label) {
            this.label = label;
            return this;
        }

        public Node setShape(Shape shape) {
            this.shape = shape;
            return this;
        }

        public Node setFontColor(Color fontColor) {
            this.fontColor = fontColor;
            return this;
        }

        public Node setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Node setWidth(int width) {
            this.width = width;
            return this;
        }

        public Node setHeight(int height) {
            this.height = height;
            return this;
        }

        public Node setColor(Color color) {
            this.color = color;
            return this;
        }

        public String toString() {
            return id + " " + "["
                   + shapeToString(shape) + ", "
                   + "width = " + width + ", "
                   + "height = " + height + ", "
                   + "color = " + color + ", "
                   + "fontcolor = " + fontColor + ", "
                   + "label = \"" + escape(label) + "\"" + ", "
                   + "fontsize = " + fontSize
                   + "]\n";
        }

        private static String shapeToString(Shape shape) {
            switch (shape) {
                case RECTANGLE:         return "shape = \"box\"";
                case ROUNDED_RECTANGLE: return "shape = \"box\" style=rounded";
                case CIRCLE:            return "shape = \"circle\"";
                case DOUBLE_CIRCLE:     return "shape = \"doublecircle\"";
                case DIAMOND:           return "shape = \"diamond\"";
            }
            throw new RuntimeException("Unknown shape");
        }
    }

    public static class Edge {

        private int sourceId;
        private int targetId;
        private String label;

        private Edge init(int sourceId, int targetId) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            return this;
        }

        public Edge setSourceId(int sourceId) {
            this.sourceId = sourceId;
            return this;
        }

        public Edge setTargetId(int targetId) {
            this.targetId = targetId;
            return this;
        }

        public Edge setLabel(String label) {
            this.label = label;
            return this;
        }

        @Override
        public String toString() {
            return sourceId + "->" + targetId + " "
                   + "["
                   + "color = black, "
                   + "style = solid, "
                   + "penwidth = 0.5, "
                   + "arrowsize = 0.7, " + ""
                   + "label = \"" + (label == null ? "" : label) + "\""
                   + "];\n";
        }
    }
}
