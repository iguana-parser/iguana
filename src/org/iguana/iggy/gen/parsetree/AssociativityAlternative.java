// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class AssociativityAlternative extends Alternative {
    public AssociativityAlternative(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    TerminalNode child0() {
       return (TerminalNode) childAt(0);
    }

    TerminalNode child1() {
       return (TerminalNode) childAt(1);
    }

    Sequence child2() {
       return (Sequence) childAt(2);
    }

    MetaSymbolNode child3() {
       return (MetaSymbolNode) childAt(3);
    }

    TerminalNode child4() {
       return (TerminalNode) childAt(4);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
