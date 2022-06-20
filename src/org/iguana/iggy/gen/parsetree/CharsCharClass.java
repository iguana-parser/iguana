// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class CharsCharClass extends CharClass {
    public CharsCharClass(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    TerminalNode child0() {
       return (TerminalNode) childAt(0);
    }

    MetaSymbolNode child1() {
       return (MetaSymbolNode) childAt(1);
    }

    TerminalNode child2() {
       return (TerminalNode) childAt(2);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
