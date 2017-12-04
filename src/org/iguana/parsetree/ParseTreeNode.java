package org.iguana.parsetree;

public interface ParseTreeNode {
    int start();
    int end();
    String text();
}
