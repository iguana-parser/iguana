// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class PrecedeSymbol extends Symbol {
    public PrecedeSymbol(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Regex child0() {
       return (Regex) childAt(0);
    }

    TerminalNode child1() {
       return (TerminalNode) childAt(1);
    }

    Symbol child2() {
       return (Symbol) childAt(2);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
