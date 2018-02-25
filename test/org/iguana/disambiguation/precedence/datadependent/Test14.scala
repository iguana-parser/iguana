package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.IGGY
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.Iguana

/**
  * Created by Anastasia Izmaylova
  */
class Test14 extends FunSuite {

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
        | E ::= '-' E
        |     > E '*' E left
        |     > E '+' E left
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
        | E(p) ::= '-' r=E(4) {4}
        |        | [3>=p] l=E(p) [l<=0||l>=3] '*' r=E(4) {3}
        |        | [2>=p] l=E(p) [l<=0||l>=2] '+' r=E(3) {2}
        |        | 'if' E(0) 'then' E(0) 'else' E(0) {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getGrammar(s)
  }

  test("Parser1") { runOriginal(Input.fromString("a + if a then a else a + a")) }
  test("Parser2") { runOriginal(Input.fromString("- if a then a else a + a")) }

//  test("DDParser1") { runDesugared(Input.fromString("a + if a then a else a + a")) }
//  test("DDParser2") { runDesugared(Input.fromString("- if a then a else a + a")) }

  private def runOriginal(input: Input) = {
    val result = Iguana.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  private def runDesugared(input: Input) = {
    val result = Iguana.parse(input, desugaredGrammar, start)
    assert(result.isParseSuccess)
    assertResult(0)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
