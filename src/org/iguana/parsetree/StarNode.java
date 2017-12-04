package org.iguana.parsetree;

import org.iguana.grammar.symbol.Star;

public interface StarNode extends ParseTreeNode, HasChildren, HasDefinition {
    @Override Star definition();
}
