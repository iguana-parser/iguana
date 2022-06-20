// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;

import org.iguana.parsetree.*;

import java.util.List;


public class MultiplicationExpression extends Expression {
    public MultiplicationExpression(RuntimeRule rule, List<ParseTreeNode> children, int start, int end) {
        super(rule, children, start, end);
    }

    Expression child0() {
       return (Expression) childAt(0);
    }

    TerminalNode child1() {
       return (TerminalNode) childAt(1);
    }

    Expression child2() {
       return (Expression) childAt(2);
    }

    // @Override
    public void accept(IggyParseTreeVisitor visitor) {
    }
}
