package org.iguana.parsetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface ParseTreeNode {
    int getStart();
    int getEnd();
    String getName();
    Object getGrammarDefinition();
    String getText();

    <T> Object accept(ParseTreeVisitor<T> visitor);

    default List<ParseTreeNode> children() {
        return Collections.emptyList();
    }

    default ParseTreeNode getChildWithName(String name) {
        for (ParseTreeNode node : children()) {
            if (name.equals(node.getName())) {
                return node;
            }
        }
        return null;
    }

    default List<ParseTreeNode> getChildrenWithName(String name) {
        List<ParseTreeNode> parseTreeNodes = new ArrayList<>();
        for (ParseTreeNode node : children()) {
            if (name.equals(node.getName())) {
                parseTreeNodes.add(node);
            }
        }
        return parseTreeNodes;
    }

    default boolean hasChild(String name) {
        return getChildWithName(name) != null;
    }

    default ParseTreeNode childAt(int i) {
        if (i < 0 || i >= children().size())
            throw new IndexOutOfBoundsException();
        return children().get(i);
    }

}
