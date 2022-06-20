// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class CallStatement extends Statement {
    public CallStatement(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    TerminalNode child0() {
       return (TerminalNode) childAt(0);
    }

    Arguments child1() {
       return (Arguments) childAt(1);
    }

    MetaSymbolNode child2() {
       return (MetaSymbolNode) childAt(2);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
