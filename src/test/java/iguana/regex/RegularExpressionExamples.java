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

package iguana.regex;

import java.util.ArrayList;
import java.util.List;

public class RegularExpressionExamples {

	/**
	 * Id ::= [a-zA-Z][a-zA-Z0-9]*
	 */
	public static RegularExpression getId() {
		Alt<CharRange> c1 = Alt.from(CharRange.in('a', 'z'), CharRange.in('A', 'Z'));
		Alt<CharRange> c2 = Alt.from(CharRange.in('a', 'z'), CharRange.in('A', 'Z'), CharRange.in('0', '9'));
		return Seq.builder(c1, Star.from(c2)).build();
	}
	
	/**
	 * Float ::= [0-9]+[.][0-9]+
	 */
	public static RegularExpression getFloat() {
		Alt<CharRange> c = Alt.from(CharRange.in('0', '9'));
		return Seq.builder(Plus.from(c), Char.from('.'), Plus.from(c)).build();
	}
	
	/**
	 * UnicodeEscape ::= "\\" [u]+ [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f];
	 */
	public static RegularExpression getJavaUnicodeEscape() {
		List<RegularExpression> regularExpressions = new ArrayList<>();

		regularExpressions.add(Seq.from("\\"));
		
		regularExpressions.add(Plus.from(Char.from('u')));
		
		Alt<CharRange> c = Alt.from(CharRange.in('0', '9'), CharRange.in('a', 'z'), CharRange.in('A', 'Z'));
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		regularExpressions.add(c);
		
		return Seq.builder(regularExpressions).build();
	}
	
	/**
	 * Character ::= ['] ![']+ ['] 
	 * 
	 * @return
	 */
	public static RegularExpression getCharacter() {
		List<RegularExpression> regularExpressions = new ArrayList<>();
		regularExpressions.add(Char.from('\''));
		regularExpressions.add(Plus.from(Alt.not(Char.from('\''))));
		regularExpressions.add(Char.from('\''));
		return Seq.builder(regularExpressions).build();
	}
	
	/**
	 * StringPart ::= !["\\]+ | "\n"
	 */
	public static RegularExpression getStringPart() {
		Char c1 = Char.from('"');
		Char c2 = Char.from('\\');
		Seq<Char> newline = Seq.from("\\n");

		return Alt.builder(Plus.from(Alt.not(c1, c2)), newline).build();
	}
	
	// "/*" (![*] | [*] !>> [/])* "*/"
	public static RegularExpression getMultilineComment() {
		
		Seq<Char> r1 = Seq.from("/*");
		Char star = Char.from('*');
		Char slash = Char.from('/');

		RegularExpression r2 = Star.from(Seq.from(star, slash));
		
		Seq<Char> r3 = Seq.from("*/");
		
		return Seq.builder(r1, r2, r3).build();
	}
	 
}
