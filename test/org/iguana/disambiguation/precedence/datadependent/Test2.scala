package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.IGGY
import org.iguana.grammar.iggy.IggyParser
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.parser.Iguana
import org.scalatest.FunSuite

/**
  * Created by Anastasia Izmaylova
  */
class Test2 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | S ::= E
        | E ::= E '+' E left
        |     > '-' E
        |     | 'a'
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  val desugaredGrammar = {
    @IGGY
    val s =
      """
        | S ::= E(0)
        | E(p) ::= [2>=p] l=E(p) [l<=0||l>=2] '+' r=E(3) {2}
        |        | '-' E(0) {1}
        |        | 'a' {0}
      """.stripMargin
  }

  test("Parser1") {
    val input = Input.fromString("a+a+a")
    val result = Iguana.parse(input, originalGrammar, Nonterminal.withName("S"))
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  test("Parser2") {
    val input = Input.fromString("-a+a")
    val result = Iguana.parse(input, originalGrammar, Nonterminal.withName("S"))
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  test("Parser3") {
    val input = Input.fromString("a+-a")
    val result = Iguana.parse(input, originalGrammar, Nonterminal.withName("S"))
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  test("Parser4") {
    val input = Input.fromString("a+-a+a")
    val result = Iguana.parse(input, originalGrammar, Nonterminal.withName("S"))
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
