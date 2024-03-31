package org.iguana.parsetree;

import org.iguana.utils.input.Input;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ErrorNode implements ParseTreeNode {

    private final Input input;
    private final int start;
    private final int end;
    private final List<ParseTreeNode> children;

    public ErrorNode(int start, int end, Input input) {
        this(start, end, input, Collections.emptyList());
    }

    public ErrorNode(int start, int end, Input input, List<ParseTreeNode> children) {
        this.start = start;
        this.end = end;
        this.input = input;
        this.children = children;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public String getName() {
        return "Error";
    }

    @Override
    public Object getGrammarDefinition() {
        return null;
    }

    @Override
    public String getText() {
        return input.subString(start, end);
    }

    @Override
    public <T> Object accept(ParseTreeVisitor<T> visitor) {
        return visitor.visitErrorNode(this);
    }

    @Override
    public List<ParseTreeNode> children() {
        return children;
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorNode)) return false;
        ErrorNode errorNode = (ErrorNode) o;
        return start == errorNode.start && end == errorNode.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
