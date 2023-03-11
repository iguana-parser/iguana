package org.iguana.grammar.exception;

import org.iguana.datadependent.ast.Expression;

@SuppressWarnings("serial")
public class AssertionFailedException extends RuntimeException {

    public AssertionFailedException(Expression expression) {
        super(String.format("Assertion failed: expression %s evaluated to false.", expression));
    }
}
