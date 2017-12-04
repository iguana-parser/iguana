package org.iguana.parsetree;

public interface HasChildren {
    Iterable<ParseTreeNode> children();
}
