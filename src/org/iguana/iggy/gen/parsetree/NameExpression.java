// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class NameExpression extends Expression {
    public NameExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    VarName child0() {
       return (VarName) childAt(0);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
