package org.iguana.parsetree;

import java.util.List;
import java.util.Set;

public interface AmbiguityNode extends ParseTreeNode {
    Set<List<ParseTreeNode>> ambiguities();
}
