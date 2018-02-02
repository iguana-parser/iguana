package org.iguana.parsetree;

public interface ParseTreeNode{
    int start();
    int end();
    String text();
    Iterable<ParseTreeNode> children();
    <R> R accept(ParseTreeVisitor<R> visitor);
    Object definition();
}
