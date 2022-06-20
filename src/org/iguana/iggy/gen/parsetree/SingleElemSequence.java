// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class SingleElemSequence extends Sequence {
    public SingleElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    MetaSymbolNode child0() {
       return (MetaSymbolNode) childAt(0);
    }

    Symbol child1() {
       return (Symbol) childAt(1);
    }

    MetaSymbolNode child2() {
       return (MetaSymbolNode) childAt(2);
    }

    MetaSymbolNode child3() {
       return (MetaSymbolNode) childAt(3);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
