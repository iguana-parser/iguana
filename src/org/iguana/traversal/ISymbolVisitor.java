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

package org.iguana.traversal;

import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Alt;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Code;
import org.iguana.grammar.symbol.CodeHolder;
import org.iguana.grammar.symbol.Conditional;
import org.iguana.grammar.symbol.Error;
import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Identifier;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Opt;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Return;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;

public interface ISymbolVisitor<T> {

    T visit(Align align);

    T visit(Block block);

    T visit(Code code);

    default T visit(CodeHolder codeHolder) { return null; }

    T visit(Error error);

    T visit(Conditional conditional);

    T visit(IfThen ifThen);

    T visit(IfThenElse ifThenElse);

    T visit(Ignore ignore);

    T visit(Nonterminal nonterminal);

    T visit(Offside offside);

    T visit(Terminal terminal);

    T visit(While whileSymbol);

    T visit(Return returnSymbol);

    T visit(Alt alt);

    T visit(Opt opt);

    T visit(Plus plus);

    T visit(Group group);

    T visit(Star star);

    T visit(Start start);

    default T visit(Identifier identifier) {
        throw new UnsupportedOperationException();
    }

}
