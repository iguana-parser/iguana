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

import iguana.utils.input.Input
import org.iguana.grammar.Grammar
import org.iguana.grammar.iggy.IggyParser
import org.iguana.grammar.patterns.PrecedencePattern
import org.iguana.grammar.precedence.OperatorPrecedence
import org.iguana.grammar.symbol.{Nonterminal, Rule, Terminal}
import org.iguana.parser.{Iguana, ParseResult}

/**
  *
  * E ::= E X    (none)
  * > E ; E  (right)
  * > - E
  * | a
  *
  * X ::= X , E
  * | , E
  *
  * @author Ali Afroozeh
  *
  */
class PrecedenceTest10 extends FunSuite {

  val E = Nonterminal.withName("E")
  val X = Nonterminal.withName("X")
  val a = Terminal.from(Character.from('a'))
  val comma = Terminal.from(Character.from(','))
  val semicolon = Terminal.from(Character.from(';'))
  val min = Terminal.from(Character.from('-'))

  val grammar = {
    val builder: Grammar.Builder = new Grammar.Builder
    val rule1: Rule = Rule.withHead(E).addSymbols(E, X).build
    builder.addRule(rule1)
    val rule2: Rule = Rule.withHead(E).addSymbols(E, semicolon, E).build
    builder.addRule(rule2)
    val rule3: Rule = Rule.withHead(E).addSymbols(min, E).build
    builder.addRule(rule3)
    val rule4: Rule = Rule.withHead(E).addSymbol(a).build
    builder.addRule(rule4)
    val rule5: Rule = Rule.withHead(X).addSymbols(X, comma, E).build
    builder.addRule(rule5)
    val rule6: Rule = Rule.withHead(X).addSymbols(comma, E).build
    builder.addRule(rule6)
    val list: java.util.List[PrecedencePattern] = new java.util.ArrayList[PrecedencePattern]
    list.add(PrecedencePattern.from(rule1, 0, rule2))
    list.add(PrecedencePattern.from(rule1, 1, rule2))
    list.add(PrecedencePattern.from(rule1, 0, rule3))
    list.add(PrecedencePattern.from(rule2, 0, rule3))
    val operatorPrecedence: OperatorPrecedence = new OperatorPrecedence(list)
    operatorPrecedence.transform(builder.build)
  }

  test("Grammar") {
    assert(grammar == expectedGrammar)
  }

  test("Parser") {
    val input: Input = Input.fromString("a,-a;a")
    val result: ParseResult = Iguana.parse(input, grammar, Nonterminal.withName("E"))
    assert(result.isParseSuccess)
    assert(result.asParseSuccess.getStatistics.getCountAmbiguousNodes == 0)
  }

  val expectedGrammar = {
    val s =
      """
        | E5 ::=   E1 X1
        |     |    E3 ';' E5
        |     |    'a'
        |
        | E  ::=   E1 X1
        |     |    E3 ';' E
        |     |    '-' E
        |     |    'a'
        |
        | X  ::=   X ',' E
        |     |    ',' E
        |
        | X1 ::=   X ',' E2
        |     |    ',' E2
        |
        | X2 ::=   X ',' E4
        |     |    ',' E4
        |
        | X3 ::=   X ',' E2
        |     |    ',' E2
        |
        | E1 ::=   E1 X2
        |     |    'a'
        |
        | X4 ::=   X ',' E4
        |     |    ',' E4
        |
        | E2 ::=   E1 X3
        |     |    '-' E
        |     |    'a'
        |
        | E3 ::=   E1 X4
        |     |    E3 ';' E5
        |     |    'a'
        |
        | E4 ::=   E1 X3
        |     |    'a'
        |
      """.stripMargin

    IggyParser.getGrammar(Input.fromString(s))
  }

}