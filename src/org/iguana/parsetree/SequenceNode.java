package org.iguana.parsetree;

import org.iguana.grammar.symbol.Sequence;

public interface SequenceNode extends ParseTreeNode, HasChildren, HasDefinition {
    @Override Sequence definition();
}
