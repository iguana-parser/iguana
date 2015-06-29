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

import java.util.stream.Collectors;

import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;

public class ToJavaRegexVIsitor implements RegularExpressionVisitor<String> {

	public String visit(Character c) {
		return c.getName();
	}
	
	public String visit(CharacterRange r) {
		return "[" + r.getName() + "]";
	}
	
	public String visit(EOF eof) {
		throw new UnsupportedOperationException();
	}
	
	public String visit(Epsilon e) {
		throw new UnsupportedOperationException();
	}
	
	public String visit(Terminal t) {
		return t.getRegularExpression().accept(this);
	}
	
	public String visit(Star s) {
		return s.accept(this) + "*";
	}
	
	public String visit(Plus p) {
		return p.accept(this) + "+";
	}

	public String visit(Opt o) {
		return o.accept(this) + "?";
	}
	
	public <E extends Symbol> String visit(Sequence<E> seq) {
		return "(?" + seq.getSymbols().stream().map(s -> s.accept(this)).collect(Collectors.joining()) + ")";
	}
	
	public <E extends Symbol> String visit(Alt<E> alt) {
		return "(?" +  alt.getSymbols().stream().map(s -> s.accept(this)).collect(Collectors.joining("|")) + ")";
	}
}
