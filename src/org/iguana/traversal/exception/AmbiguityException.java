package org.iguana.traversal.exception;

import iguana.utils.input.Input;
import org.iguana.sppf.NonPackedNode;

public class AmbiguityException extends RuntimeException {

    private final NonPackedNode node;
    private final Input input;

    public AmbiguityException(NonPackedNode node, Input input) {
        this.node = node;
        this.input = input;
    }

    public NonPackedNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        int lineNumber = input.getLineNumber(node.getIndex());
        int columnNumber = input.getColumnNumber(node.getIndex());
        return String.format("Ambiguity found for node %s at (%d, %d)", node, lineNumber, columnNumber);
    }
}
