// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class PostConditionSymbol extends Symbol {
    public PostConditionSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Symbol child0() {
       return (Symbol) childAt(0);
    }

    Condition child1() {
       return (Condition) childAt(1);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
