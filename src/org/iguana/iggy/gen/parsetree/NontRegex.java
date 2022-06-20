// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class NontRegex extends Regex {
    public NontRegex(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Name child0() {
       return (Name) childAt(0);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
