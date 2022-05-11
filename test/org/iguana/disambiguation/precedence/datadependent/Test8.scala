package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  * Created by Anastasia Izmaylova
  */
class Test8 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | S ::= E
        | E ::= E 'z'
        |     > 'x' E
        |     > E 'w'
        |     > 'y' E
        |     | 'a'
      """.stripMargin
    IggyParser.getRuntimeGrammar(s)
  }

  val start = Nonterminal.withName("S")

  // TODO: Extend syntax of IGGY
  val desugaredGrammar = {
    @IGGY
    val s =
      """
        | S ::= E(0)
        | E(p) ::= [4>=p] l=E(p) [l<=0||l>=4] 'z' {0}
        |        | 'x' r=E(3) {3}
        |        | [2>=p] l=E(p) [l<=0||l>=2] 'w' {0}
        |        | 'y' E(0) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getRuntimeGrammar(s)
  }

  test("Parser1") { runOriginal(Input.fromString("xawz")); }
  test("Parser2") { runOriginal(Input.fromString("xyaw")); }

//  test("DDParser1") { runDesugared(Input.fromString("xawz")) }
//  test("DDParser2") { runDesugared(Input.fromString("xyaw")) }

  private def runOriginal(input: Input) = {
    val result = IguanaParser.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  private def runDesugared(input: Input) = {
    val result = IguanaParser.parse(input, desugaredGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
