// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class RegexRule extends NonterminalNode {
    public RegexRule(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Name child0() {
       return (Name) childAt(0);
    }

    TerminalNode child1() {
       return (TerminalNode) childAt(1);
    }

    RegexBody child2() {
       return (RegexBody) childAt(2);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
