/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.datadependent.traversal;

import org.iguana.datadependent.ast.AST;
import org.iguana.datadependent.ast.Expression.*;
import org.iguana.datadependent.ast.Expression.Boolean;
import org.iguana.datadependent.ast.Expression.Integer;
import org.iguana.datadependent.ast.Expression.String;
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.VariableDeclaration;

public interface IAbstractASTVisitor<T> {

    T visit(Boolean expression);

    T visit(Integer expression);

    T visit(Real expression);

    T visit(String expression);

    T visit(Not not);

    T visit(Tuple expression);

    default T visit(IntTuple2 expression) {
        return AST.tuple(expression.getElement1(), expression.getElement2()).accept(this);
    }

    T visit(Name expression);

    T visit(Call expression);

    T visit(Assignment expression);

    T visit(LShiftANDEqZero expression);

    T visit(OrIndent expression);

    T visit(AndIndent expression);

    T visit(Or expression);

    T visit(And expression);

    T visit(Less expression);

    T visit(LessThanEqual expression);

    T visit(Greater expression);

    T visit(GreaterThanEqual expression);

    T visit(Equal expression);

    T visit(NotEqual expression);

    T visit(Add expression);

    T visit(Subtract expression);

    T visit(Multiply expression);

    T visit(Divide expression);

    T visit(LeftExtent expression);

    T visit(RightExtent expression);

    T visit(Yield expression);

    T visit(Val expression);

    T visit(EndOfFile expression);

    T visit(IfThenElse expression);

    T visit(VariableDeclaration declaration);

    T visit(Statement.Expression statement);

    T visit(Statement.VariableDeclaration statement);

}
