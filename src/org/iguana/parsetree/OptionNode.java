package org.iguana.parsetree;

import org.iguana.grammar.symbol.Opt;

public interface OptionNode extends ParseTreeNode, HasChildren, HasDefinition {
    ParseTreeNode getNode();
    boolean hasNode();
    @Override Opt definition();
}
