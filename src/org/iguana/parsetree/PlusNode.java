package org.iguana.parsetree;

import org.iguana.grammar.symbol.Plus;

public interface PlusNode extends ParseTreeNode, HasChildren, HasDefinition {
    @Override Plus definition();
}
