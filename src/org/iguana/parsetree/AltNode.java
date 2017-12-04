package org.iguana.parsetree;

import org.iguana.grammar.symbol.Alt;

public interface AltNode extends ParseTreeNode, HasChildren, HasDefinition {
    @Override Alt definition();
}
