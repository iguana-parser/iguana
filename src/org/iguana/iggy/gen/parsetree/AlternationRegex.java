// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class AlternationRegex extends Regex {
    public AlternationRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    TerminalNode child0() {
       return (TerminalNode) childAt(0);
    }

    Regexs child1() {
       return (Regexs) childAt(1);
    }

    MetaSymbolNode child2() {
       return (MetaSymbolNode) childAt(2);
    }

    TerminalNode child3() {
       return (TerminalNode) childAt(3);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
