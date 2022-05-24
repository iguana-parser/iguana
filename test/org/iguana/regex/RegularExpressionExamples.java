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

package org.iguana.regex;

import java.util.ArrayList;
import java.util.List;

public class RegularExpressionExamples {

    /**
     * Id ::= [a-zA-Z][a-zA-Z0-9]*
     */
    public static RegularExpression getId() {
        org.iguana.regex.Alt<org.iguana.regex.CharRange> c1 = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('a', 'z'), org.iguana.regex.CharRange.in('A', 'Z'));
        org.iguana.regex.Alt<org.iguana.regex.CharRange> c2 = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('a', 'z'), org.iguana.regex.CharRange.in('A', 'Z'), org.iguana.regex.CharRange.in('0', '9'));
        return org.iguana.regex.Seq.builder(c1, org.iguana.regex.Star.from(c2)).build();
    }

    /**
     * Float ::= [0-9]+[.][0-9]+
     */
    public static RegularExpression getFloat() {
        org.iguana.regex.Alt<org.iguana.regex.CharRange> c = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('0', '9'));
        return org.iguana.regex.Seq.builder(org.iguana.regex.Plus.from(c), org.iguana.regex.Char.from('.'), org.iguana.regex.Plus.from(c)).build();
    }

    /**
     * UnicodeEscape ::= "\\" [u]+ [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f] [0-9 A-F a-f];
     */
    public static RegularExpression getJavaUnicodeEscape() {
        List<RegularExpression> regularExpressions = new ArrayList<>();

        regularExpressions.add(org.iguana.regex.Seq.from("\\"));

        regularExpressions.add(org.iguana.regex.Plus.from(org.iguana.regex.Char.from('u')));

        org.iguana.regex.Alt<org.iguana.regex.CharRange> c = org.iguana.regex.Alt.from(org.iguana.regex.CharRange.in('0', '9'), org.iguana.regex.CharRange.in('a', 'z'), CharRange.in('A', 'Z'));
        regularExpressions.add(c);
        regularExpressions.add(c);
        regularExpressions.add(c);
        regularExpressions.add(c);

        return org.iguana.regex.Seq.builder(regularExpressions).build();
    }

    /**
     * Character ::= ['] ![']+ [']
     *
     * @return
     */
    public static RegularExpression getCharacter() {
        List<RegularExpression> regularExpressions = new ArrayList<>();
        regularExpressions.add(org.iguana.regex.Char.from('\''));
        regularExpressions.add(org.iguana.regex.Plus.from(org.iguana.regex.Alt.not(org.iguana.regex.Char.from('\''))));
        regularExpressions.add(org.iguana.regex.Char.from('\''));
        return org.iguana.regex.Seq.builder(regularExpressions).build();
    }

    /**
     * StringPart ::= !["\\]+ | "\n"
     */
    public static RegularExpression getStringPart() {
        org.iguana.regex.Char c1 = org.iguana.regex.Char.from('"');
        org.iguana.regex.Char c2 = org.iguana.regex.Char.from('\\');
        org.iguana.regex.Seq<org.iguana.regex.Char> newline = org.iguana.regex.Seq.from("\\n");

        return org.iguana.regex.Alt.builder(Plus.from(org.iguana.regex.Alt.not(c1, c2)), newline).build();
    }

    // "/*" (![*] | [*] !>> [/])* "*/"
    public static RegularExpression getMultilineComment() {
        return org.iguana.regex.Seq.builder()
                .add(org.iguana.regex.Seq.from("/*"))
                .add(Star.from(org.iguana.regex.Alt.from(
                                Alt.not(org.iguana.regex.Char.from('*')),
                                org.iguana.regex.Char.builder('*').addLookahead(Char.from('/')).build())))
                .add(Seq.from("*/"))
                .build();
    }

}
