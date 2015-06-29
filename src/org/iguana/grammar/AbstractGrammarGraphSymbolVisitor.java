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

package org.iguana.grammar;

import org.iguana.grammar.exception.UnexpectedSymbol;
import org.iguana.grammar.symbol.Align;
import org.iguana.grammar.symbol.Block;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.IfThen;
import org.iguana.grammar.symbol.IfThenElse;
import org.iguana.grammar.symbol.Ignore;
import org.iguana.grammar.symbol.Offside;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.grammar.symbol.While;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.traversal.ISymbolVisitor;

public abstract class AbstractGrammarGraphSymbolVisitor implements ISymbolVisitor<Void> {
	
	@Override
	public Void visit(Align symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}
	
	@Override
	public Void visit(Block symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	public abstract Void visit(RegularExpression symbol);

	@Override
	public Void visit(Character symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(CharacterRange symbol) {
		return visit((RegularExpression) symbol);
	}
	
		@Override
	public Void visit(EOF symbol) {
		return visit((RegularExpression) symbol);
	}

	@Override
	public Void visit(Epsilon symbol) {
		return visit((RegularExpression) symbol);
	}
	
	@Override
	public Void visit(IfThen symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}
	
	@Override
	public Void visit(IfThenElse symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public Void visit(Terminal symbol) {
		return visit((RegularExpression) symbol);
	}
	
	@Override
	public Void visit(Ignore symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}
	
	@Override
	public Void visit(Offside symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}
	
	@Override
	public Void visit(While symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public <E extends Symbol> Void visit(Alt<E> symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public Void visit(Opt symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public Void visit(Plus symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public <E extends Symbol> Void visit(Sequence<E> symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

	@Override
	public Void visit(Star symbol) {
		throw new UnexpectedSymbol(symbol, "grammar-to-graph transformation");
	}

}
