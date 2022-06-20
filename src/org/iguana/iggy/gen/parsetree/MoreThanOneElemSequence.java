// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class MoreThanOneElemSequence extends Sequence {
    public MoreThanOneElemSequence(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    MetaSymbolNode child0() {
       return (MetaSymbolNode) childAt(0);
    }

    MetaSymbolNode child1() {
       return (MetaSymbolNode) childAt(1);
    }

    Symbol child2() {
       return (Symbol) childAt(2);
    }

    MetaSymbolNode child3() {
       return (MetaSymbolNode) childAt(3);
    }

    MetaSymbolNode child4() {
       return (MetaSymbolNode) childAt(4);
    }

    MetaSymbolNode child5() {
       return (MetaSymbolNode) childAt(5);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
