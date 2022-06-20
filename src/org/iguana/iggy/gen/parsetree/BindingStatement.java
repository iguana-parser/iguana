// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class BindingStatement extends Statement {
    public BindingStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Binding child0() {
       return (Binding) childAt(0);
    }

    MetaSymbolNode child1() {
       return (MetaSymbolNode) childAt(1);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
