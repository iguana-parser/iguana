// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class ContextFreeRule extends Rule {
    public ContextFreeRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    MetaSymbolNode child0() {
       return (MetaSymbolNode) childAt(0);
    }

    Name child1() {
       return (Name) childAt(1);
    }

    MetaSymbolNode child2() {
       return (MetaSymbolNode) childAt(2);
    }

    TerminalNode child3() {
       return (TerminalNode) childAt(3);
    }

    Body child4() {
       return (Body) childAt(4);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
