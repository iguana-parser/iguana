package org.iguana.parsetree;

import org.iguana.grammar.symbol.Opt;

public interface OptionNode extends ParseTreeNode<Opt> {
    ParseTreeNode<?> getNode();
    boolean hasNode();
}
