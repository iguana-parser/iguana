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

import java.util.List;
import java.util.Map;
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
		return getPreConditions(c) + getChar(c.getValue()) + getPostConditions(c);
	}
	
	public String visit(CharacterRange r) {
		return getPreConditions(r) + "[" + getRange(r) + "]" + getPostConditions(r);
	}
	
	public String visit(EOF eof) {
		throw new UnsupportedOperationException();
	}
	
	public String visit(Epsilon e) {
		return "";
	}
	
	public String visit(Terminal t) {
		return getPreConditions(t) + t.getRegularExpression().accept(this) + getPostConditions(t);
	}
	
	public String visit(Star s) {
		return getPreConditions(s) + s.getSymbol().accept(this) + "*" + getPostConditions(s);
	}
	
	public String visit(Plus p) {
		return getPreConditions(p) + p.getSymbol().accept(this) + "+" + getPostConditions(p);
	}

	public String visit(Opt o) {
		return getPreConditions(o) + o.getSymbol().accept(this) + "?" + getPostConditions(o);
	}
	
	public <E extends Symbol> String visit(Sequence<E> seq) {
		return getPreConditions(seq) + "(?:" + seq.getSymbols().stream().map(s -> s.accept(this)).collect(Collectors.joining()) + ")" + getPostConditions(seq);
	}
	
	public <E extends Symbol> String visit(Alt<E> alt) {
		Map<Boolean, List<E>> parition = alt.getSymbols().stream().collect(Collectors.partitioningBy(s -> isCharClass(s)));
		List<E> charClasses = parition.get(true);
		List<E> other = parition.get(false);

		StringBuilder sb = new StringBuilder();

		if (!charClasses.isEmpty() && !other.isEmpty()) {
			int left  = charClasses.size();
			int right = other.stream().map(s -> (RegularExpression) s).mapToInt(r -> r.length()).max().getAsInt();
			
			sb.append("(?:");
			if (left > right) {
				sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
				sb.append("|");
				sb.append(other.stream().map(s -> s.accept(this)).collect(Collectors.joining("|")));
			} else {
				sb.append(other.stream().sorted(RegularExpression.lengthComparator()).map(s -> s.accept(this)).collect(Collectors.joining("|")));
				sb.append("|");
				sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
			}
			sb.append(")");
		} 
		else if (!charClasses.isEmpty()) {
			sb.append("[" + charClasses.stream().map(s -> asCharClass(s)).collect(Collectors.joining()) + "]");
		} 
		else {
			sb.append("(?:" + other.stream().sorted(RegularExpression.lengthComparator()).map(s -> s.accept(this)).collect(Collectors.joining("|")) + ")");
	    }
		
		return getPreConditions(alt) + sb.toString() + getPostConditions(alt);
	}
	
	private boolean isCharClass(Symbol s) {
		if (!s.getPostConditions().isEmpty()) return false;
		return s instanceof Character || s instanceof CharacterRange;
	}
	
	private String asCharClass(Symbol s) {
		if (s instanceof Character) {
			Character c = (Character) s;
			return getChar(c.getValue()) + getPostConditions(c);
		} 
		else if (s instanceof CharacterRange) {
			CharacterRange r = (CharacterRange) s;
			return getRange(r) + getPostConditions(r);
		}
		
		throw new RuntimeException(s + " is not a character or character class.");
	}
	
	private String getPostConditions(RegularExpression regex) {
		return regex.getPostConditions().stream().map(c -> getCondition(c)).collect(Collectors.joining());
	}
	
	private String getPreConditions(RegularExpression regex) {
		return regex.getPreConditions().stream().map(c -> getCondition(c)).collect(Collectors.joining());
	}

	
	private String getCondition(Condition condition) {
		switch (condition.getType()) {
			case NOT_FOLLOW: 
				return "(?!" + getRegularExpression(condition).accept(this) + ")";
				
			case NOT_PRECEDE:
				return "(?<!" + getRegularExpression(condition).accept(this) + ")";
				
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
		return getChar(r.getStart()) + "-" + getChar(r.getEnd());
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
