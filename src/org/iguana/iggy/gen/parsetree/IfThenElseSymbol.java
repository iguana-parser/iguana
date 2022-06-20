// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class IfThenElseSymbol extends Symbol {
    public IfThenElseSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    TerminalNode child0() {
       return (TerminalNode) childAt(0);
    }

    Expression child1() {
       return (Expression) childAt(1);
    }

    Symbol child2() {
       return (Symbol) childAt(2);
    }

    TerminalNode child3() {
       return (TerminalNode) childAt(3);
    }

    Symbol child4() {
       return (Symbol) childAt(4);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
