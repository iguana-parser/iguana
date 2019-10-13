package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  * Created by Anastasia Izmaylova
  */
class Test11 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | S ::= E
        | E ::= E '*' E left
        |     > E '+' E left
        |     | 'a'
      """.stripMargin
    IggyParser.getRuntimeGrammar(s)
  }

  val start = Nonterminal.withName("S")

  val desugaredGrammar = {
    @IGGY
    val s =
      """
        | S ::= E(0)
        | E(p) ::= [2>=p] l=E(p) [l<=0||l>=2] '*' E(3) {2}
        |        | [1>=p] l=E(p) [l<=0||l>=1] '+' E(2) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getRuntimeGrammar(s)
  }

  test("Parser") {
    val input = Input.fromString("a+a*a+a*a*a+a+a")
    val result = IguanaParser.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  test("DDParser") {
    val input = Input.fromString("a+a*a+a*a*a+a+a")
    val result = IguanaParser.parse(input, desugaredGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
