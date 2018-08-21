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

import java.util.Arrays

import iguana.utils.input.Input
import org.iguana.grammar.Grammar
import org.iguana.grammar.patterns.PrecedencePattern
import org.iguana.grammar.precedence.OperatorPrecedence
import org.iguana.grammar.symbol.{Nonterminal, Rule, Terminal}
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  *
  * E ::= - E
  * > E + E   left
  * > * E
  * | a
  *
  *
  * @author Ali Afroozeh
  *
  */
class PrecedenceTest12 extends FunSuite {

  val E = Nonterminal.withName("E")
  val plus = Terminal.from(Character.from('+'))
  val star = Terminal.from(Character.from('*'))
  val minus = Terminal.from(Character.from('-'))
  val a = Terminal.from(Character.from('a'))

  val grammar: Grammar = createGrammar

  def createGrammar = {
    val builder: Grammar.Builder = new Grammar.Builder
    val rule1: Rule = Rule.withHead(E).addSymbols(minus, E).build
    val rule2: Rule = Rule.withHead(E).addSymbols(E, plus, E).build
    val rule3: Rule = Rule.withHead(E).addSymbols(star, E).build
    val rule4: Rule = Rule.withHead(E).addSymbol(a).build

    builder.addRule(rule1)
    builder.addRule(rule2)
    builder.addRule(rule3)
    builder.addRule(rule4)

    val list: java.util.List[PrecedencePattern] = Arrays.asList(PrecedencePattern.from(rule1, 1, rule2),
                                                                PrecedencePattern.from(rule2, 0, rule3),
                                                                PrecedencePattern.from(rule2, 2, rule2))
    val operatorPrecedence: OperatorPrecedence = new OperatorPrecedence(list)

    operatorPrecedence.transform(builder.build)
  }

  test("grammar") {
    assert(getGrammar == grammar)
  }

  test("parser") {
    val input: Input = Input.fromString("-*a+a")
    val result: ParseResult = IguanaParser.parse(input, grammar, Nonterminal.withName("E"))
    assert(result.asParseSuccess.getStatistics.getCountAmbiguousNodes == 0)
    assert(result.isParseSuccess)
  }

  private def getGrammar: Grammar = {
    val s = """
              | E1 ::= '-' E1
              |	    | '*' E
              |	    | 'a'
              |
              | E2 ::= '-' E3
              |     | E2 '+' E3
              |     | 'a'
              |
              | E3 ::= '-' E3
              |     | 'a'
              |
              | E ::= '-' E1
              |     | E2 '+' E1
              |     | '*' E
              |     | 'a'
              |
              """.stripMargin
    IggyParser.getGrammar(Input.fromString(s))
  }
}