package org.iguana.parsetree;

public interface ParseTreeNode<T> {
    int start();
    int end();
    String text();
    Iterable<? extends ParseTreeNode<?>> children();
    <R> R accept(ParseTreeVisitor<R> visitor);
    T definition();
}
