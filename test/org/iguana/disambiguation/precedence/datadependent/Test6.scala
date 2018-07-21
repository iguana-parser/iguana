package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.IGGY
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  * Created by Anastasia Izmaylova
  */
class Test6 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | S ::= E
        | E ::= '-' E
        |     > E '+' E left
        |     > '*' E
        |     | 'a'
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  val start = Nonterminal.withName("S")

  // TODO: Extend syntax of IGGY
  val desugaredGrammar = {
    @IGGY
    val s =
      """
        | S ::= E(0)
        | E(p) ::= '-' r=E(3) {3}
        |        | [2>=p] l=E(p) [l<=0||l>=2] '+' r=E(3) {2}
        |        | '*' E(0) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  test("Parser") {
    val input = Input.fromString("-*a+a")
    val result = IguanaParser.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

//  test("DDParser") {
//    val input = Input.fromString("-*a+a")
//    val result = IguanaParser.parse(input, desugaredGrammar, start)
//    assert(result.isParseSuccess)
//    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
//  }

}
