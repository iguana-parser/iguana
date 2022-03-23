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

import org.iguana.grammar.symbol.*;

public interface ISymbolVisitor<T> {
	
	T visit(Align symbol);
	
	T visit(Block symbol);
	
	T visit(Code symbol);

	default T visit(CodeHolder symbol) { return null; }

	T visit(Conditional symbol);
	
	T visit(IfThen symbol);
	
    T visit(IfThenElse symbol);
	
	T visit(Ignore symbol);
	
	T visit(Nonterminal symbol);
	
    T visit(Offside symbol);
	
	T visit(Terminal symbol);
	
	T visit(While symbol);
	
	T visit(Return symbol);
	
	T visit(Alt symbol);
	
	T visit(Opt symbol);
	
	T visit(Plus symbol);
	
	T visit(Group symbol);
	
	T visit(Star symbol);

	T visit(Start start);

	default T visit(Identifier identifier) {
		throw new UnsupportedOperationException();
	}

}
