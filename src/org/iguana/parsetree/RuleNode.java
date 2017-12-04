package org.iguana.parsetree;


import org.iguana.grammar.symbol.Rule;

public interface RuleNode extends ParseTreeNode, HasChildren {
    ParseTreeNode childAt(int i);
    ParseTreeNode childWithName(String name);
    Rule rule();
}
