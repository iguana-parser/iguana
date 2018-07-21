package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.IGGY
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  * Created by Anastasia Izmaylova
  */
class Test13 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | regex {
        | Whitespaces ::= [\t\n\r\ ]*
        | }
        |
        | @Layout
        | L ::= Whitespaces
        |
        | S ::= E
        | E ::= E '+' E left
        |     > 'if' E 'then' E 'else' E
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
        | regex {
        | Whitespaces ::= [\t\n\r\ ]*
        | }
        |
        | @Layout
        | L ::= Whitespaces
        |
        | S ::= E(0)
        | E(p) ::= [2>=p] l=E(p) [l<=0||l>=2] '+' E(3) {2}
        |        | 'if' E(0) 'then' E(0) 'else' E(0) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  test("Parser") {
    val input = Input.fromString("a + if a then a else a + a")
    val result = IguanaParser.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

//  test("DDParser") {
//    val input = Input.fromString("a + if a then a else a + a")
//    val result = IguanaParser.parse(input, desugaredGrammar, start)
//    assert(result.isParseSuccess)
//    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
//  }

}
