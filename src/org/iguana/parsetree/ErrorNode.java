package org.iguana.parsetree;

import org.iguana.utils.input.Input;

import java.util.Collections;
import java.util.List;

public class ErrorNode implements ParseTreeNode {

    private final Input input;
    private final int start;
    private final int end;

    public ErrorNode(int start, int end, Input input) {
        this.start = start;
        this.end = end;
        this.input = input;
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
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }
}
