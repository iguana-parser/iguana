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

import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.Character;
import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.grammar.symbol.EOF;
import org.iguana.grammar.symbol.Epsilon;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.regex.Alt;
import org.iguana.regex.Opt;
import org.iguana.regex.Plus;
import org.iguana.regex.RegularExpression;
import org.iguana.regex.Sequence;
import org.iguana.regex.Star;
import org.iguana.util.unicode.UnicodeUtil;

public class ToJavaRegexVisitor implements RegularExpressionVisitor<String> {

	public String visit(Character c) {
		return getChar(c.getValue()) + getConditions(c);
	}
	
	public String visit(CharacterRange r) {
		return getRange(r) + getConditions(r);
	}
	
	public String visit(EOF eof) {
		throw new UnsupportedOperationException();
	}
	
	public String visit(Epsilon e) {
		return "";
	}
	
	public String visit(Terminal t) {
		return t.getRegularExpression().accept(this) + getConditions(t);
	}
	
	public String visit(Star s) {
		return s.getSymbol().accept(this) + "*+" + getConditions(s);
	}
	
	public String visit(Plus p) {
		return p.getSymbol().accept(this) + "++" + getConditions(p);
	}

	public String visit(Opt o) {
		return o.getSymbol().accept(this) + "?+" + getConditions(o);
	}
	
	public <E extends Symbol> String visit(Sequence<E> seq) {
		return "(?:" + seq.getSymbols().stream().map(s -> s.accept(this)).collect(Collectors.joining()) + ")" + getConditions(seq);
	}
	
	public <E extends Symbol> String visit(Alt<E> alt) {
		return "(?:" +  alt.getSymbols().stream().sorted(RegularExpression.lengthComparator()).map(s -> s.accept(this)).collect(Collectors.joining("|")) + ")" + getConditions(alt);
	}
	
	private String getConditions(RegularExpression regex) {
		return regex.getPostConditions().stream().map(c -> getCondition(c)).collect(Collectors.joining());
	}
	
	private String getCondition(Condition condition) {
		switch (condition.getType()) {
			case NOT_FOLLOW: return "(?!" + getRegularExpression(condition).accept(this) + ")";
				
			default: return "";
		}
	}
	
	private static RegularExpression getRegularExpression(Condition condition) {
		if (condition instanceof RegularExpressionCondition) {
			return ((RegularExpressionCondition) condition).getRegularExpression();
		}
		throw new UnsupportedOperationException();
	}

	private String getChar(int c) {
		if(UnicodeUtil.isPrintableAscii(c))
			return escape((char) c + "");			
		else
			return escape(String.format("\\x{%04X}", c));
	}
	
	private String getRange(CharacterRange r) {
		return String.format("[%s-%s]", getChar(r.getStart()), getChar(r.getEnd()));
	}
	
	private String escape(String s) {
		String backslash = "\\";
		
		switch (s) {
			case "(":
			case ")":
			case "[":
			case "]":
			case "{":
			case "}":
			case "\\":
			case "^":
			case "-":
			case "=":
			case "$":
			case "!":
			case "|":
			case "?":
			case "*":
			case "+":
			case ".":
				return backslash + s;
				
			default:
				return s;
		}
	}
	
}
