package org.iguana.parsetree;

import java.util.List;

public interface ParseTreeNode {
    int getStart();
    int getEnd();
    String getName();
    Object getGrammarDefinition();
    String getText();

    <T> Object accept(ParseTreeVisitor<T> visitor);

    List<ParseTreeNode> children();

    boolean hasChildren();

    default ParseTreeNode childAt(int i) {
        if (i < 0 || i >= children().size())
            throw new IndexOutOfBoundsException();
        return children().get(i);
    }
}
