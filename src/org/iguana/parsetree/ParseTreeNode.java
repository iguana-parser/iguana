package org.iguana.parsetree;

import iguana.utils.input.Input;

public interface ParseTreeNode{
    int start();
    int end();
    String text(Input input);
    Iterable<ParseTreeNode> children();
    <R> R accept(ParseTreeVisitor<R> visitor);
    Object definition();
}
