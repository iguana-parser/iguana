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
package org.iguana.disambiguation.precedence

import org.iguana.grammar.iggy.IggyParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.util.ArrayList
import java.util.List
import org.iguana.grammar.Grammar
import org.iguana.grammar.patterns.PrecedencePattern
import org.iguana.grammar.precedence.OperatorPrecedence
import org.iguana.grammar.symbol.Terminal
import iguana.regex.Character
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.grammar.symbol.Rule
import org.iguana.parser.Iguana
import org.iguana.parser.ParseResult
import org.iguana.util.Configuration
import iguana.utils.input.Input
import org.scalatest.FunSuite

/**
  *
  * E ::= E ^ E	(right)
  * > E + E	(left)
  * > - E
  * | a
  *
  *
  * @author Ali Afroozeh
  *
  */
class PrecedenceTest2 extends FunSuite {

  val E = Nonterminal.withName("E")
  val a = Terminal.from(Character.from('a'))
  val hat = Terminal.from(Character.from('^'))
  val plus = Terminal.from(Character.from('+'))
  val minus = Terminal.from(Character.from('-'))

  val grammar = {
    val builder: Grammar.Builder = new Grammar.Builder
    val rule0: Rule = Rule.withHead(E).addSymbols(E, hat, E).build
    builder.addRule(rule0)
    val rule1: Rule = Rule.withHead(E).addSymbols(E, plus, E).build
    builder.addRule(rule1)
    val rule2: Rule = Rule.withHead(E).addSymbols(minus, E).build
    builder.addRule(rule2)
    val rule3: Rule = Rule.withHead(E).addSymbols(a).build
    builder.addRule(rule3)
    val list: java.util.List[PrecedencePattern] = new java.util.ArrayList[PrecedencePattern]
    list.add(PrecedencePattern.from(rule1, 2, rule1))
    list.add(PrecedencePattern.from(rule1, 0, rule2))
    list.add(PrecedencePattern.from(rule0, 0, rule0))
    list.add(PrecedencePattern.from(rule0, 0, rule2))
    list.add(PrecedencePattern.from(rule0, 0, rule1))
    list.add(PrecedencePattern.from(rule0, 2, rule1))
    val operatorPrecedence: OperatorPrecedence = new OperatorPrecedence(list)
    operatorPrecedence.transform(builder.build)
  }

  test("Grammar") {
    assert(expectedGrammar == grammar)
  }

  test("Parser") {
    val input: Input = Input.fromString("a+a^a^-a+a")
    val result: ParseResult = Iguana.parse(input, grammar, Nonterminal.withName("E"))
    assert(result.isParseSuccess)
    assert(result.asParseSuccess.getStatistics.getCountAmbiguousNodes == 0)
  }

  val expectedGrammar = {
    val s =
      """
        | E ::= E3 '^' E1
        |     | E2 '+' E1
        |     | '-' E
        |     | 'a'
        |
        | E1 ::= E3 '^' E1
        |      | '-' E
        |      | 'a'
        |
        | E2 ::= E3 '^' E4
        |      | E2 '+' E4
        |      | 'a'
        |
        | E3 ::= 'a'
        |
        | E4 ::= E3 '^' E4
        |      | 'a'
      """.stripMargin

    IggyParser.getGrammar(Input.fromString(s))
  }
}