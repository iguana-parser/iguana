package org.iguana.parsetree;

import org.iguana.grammar.symbol.Terminal;

public interface TerminalNode extends ParseTreeNode, HasDefinition {
    @Override Terminal definition();
}
