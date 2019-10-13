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
import org.iguana.grammar.RuntimeGrammar
import org.iguana.grammar.patterns.{ExceptPattern, PrecedencePattern}
import org.iguana.grammar.precedence.OperatorPrecedence
import org.iguana.grammar.runtime.RuntimeRule
import org.iguana.grammar.symbol.{Nonterminal, Terminal}
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  *
  * E ::= E Plus    (non-assoc)
  * > E + E	 (left)
  * | a
  *
  * EPlus ::= EPlus E
  * | E
  *
  */
class PrecedenceTest3 extends FunSuite {

  val E = Nonterminal.withName("E")
  val EPlus = new Nonterminal.Builder("EPlus").setEbnfList(true).build
  val a = Terminal.from(Character.from('a'))
  val plus = Terminal.from(Character.from('+'))

  private val grammar = {
    val builder: RuntimeGrammar.Builder = new RuntimeGrammar.Builder
    val rule1: RuntimeRule = RuntimeRule.withHead(E).addSymbols(E, EPlus).build
    builder.addRule(rule1)
    val rule2: RuntimeRule = RuntimeRule.withHead(E).addSymbols(E, plus, E).build
    builder.addRule(rule2)
    val rule3: RuntimeRule = RuntimeRule.withHead(E).addSymbols(a).build
    builder.addRule(rule3)
    val rule4: RuntimeRule = RuntimeRule.withHead(EPlus).addSymbols(EPlus, E).build
    builder.addRule(rule4)
    val rule5: RuntimeRule = RuntimeRule.withHead(EPlus).addSymbols(E).build
    builder.addRule(rule5)
    val precedence: java.util.List[PrecedencePattern] = new java.util.ArrayList[PrecedencePattern]
    precedence.add(PrecedencePattern.from(rule1, 0, rule1))
    precedence.add(PrecedencePattern.from(rule1, 1, rule1))
    precedence.add(PrecedencePattern.from(rule1, 0, rule2))
    precedence.add(PrecedencePattern.from(rule1, 1, rule2))
    precedence.add(PrecedencePattern.from(rule2, 2, rule2))
    val except: java.util.List[ExceptPattern] = new java.util.ArrayList[ExceptPattern]
    except.add(ExceptPattern.from(rule4, 1, rule1))
    except.add(ExceptPattern.from(rule4, 1, rule2))
    except.add(ExceptPattern.from(rule5, 0, rule1))
    except.add(ExceptPattern.from(rule5, 0, rule2))
    val operatorPrecedence: OperatorPrecedence = new OperatorPrecedence(precedence, except)
    operatorPrecedence.transform(builder.build)
  }

  test("Parser") {
    val input = Input.fromString("aaa+aaaaa+aaaa")
    val result: ParseResult = IguanaParser.parse(input, grammar, Nonterminal.withName("E"))
    assert(result.isParseSuccess)
    assert(result.asParseSuccess.getStatistics.getCountAmbiguousNodes == 0)
  }

  test("Grammar") {
    assert(expectedGrammar == grammar)
  }

  val expectedGrammar = {
    val s =
      """
        | E ::= E1 EPlus1
        |     | E '+' E2
        |     | 'a'
        |
        | EPlus1 ::= EPlus E3
        |          | E3
        |
        | EPlus2 ::= EPlus E3
        |          | E3
        |
        | E1 ::= 'a'
        |
        | EPlus ::= EPlus E3
        |         | E3
        |
        | E2 ::= E1 EPlus2
        |      | 'a'
        |
        | E3 ::= 'a'
        |
      """.stripMargin

    IggyParser.getRuntimeGrammar(Input.fromString(s))
  }

}