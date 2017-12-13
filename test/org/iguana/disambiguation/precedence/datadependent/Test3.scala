package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.IGGY
import org.iguana.grammar.iggy.IggyParser
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.parser.Iguana

/**
  * Created by Anastasia Izmaylova
  */
class Test3 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | S ::= E
        | E ::= E '+' E left
        |     | 'a'
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  val start = Nonterminal.withName("S")

  val desugaredGrammar = {
    @IGGY
    val s =
      """
        | S ::= E(0)
        | E(p) ::= [1>=p] l=E(p) [l<=0||l>=1] '+' E(2) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  test("Parser") {
    val input = Input.fromString("a+a+a")
    val result = Iguana.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  test("DDParser") {
    val input = Input.fromString("a+a+a")
    val result = Iguana.parse(input, desugaredGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
