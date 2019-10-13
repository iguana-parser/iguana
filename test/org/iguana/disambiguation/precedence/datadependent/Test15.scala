package org.iguana.disambiguation.precedence.datadependent

import iguana.utils.input.Input
import org.iguana.grammar.symbol.Nonterminal
import org.iguana.iggy.IggyParser
import org.iguana.parser.IguanaParser

/**
  * Created by Anastasia Izmaylova
  */
class Test15 extends FunSuite {

  val originalGrammar = {
    @IGGY
    val s =
      """
        | regex {
        | Whitespaces ::= [\t\n\r\ ]*
        | Id ::= 'id'
        | }
        |
        | @Layout
        | L ::= Whitespaces
        |
        | S ::= E
        | E ::= E '.' Id
        |     > E E                        left
        |     > '-' E
        |     > E '*' E                    left
        |     > (E '+' E |  E '-' E)       left
        |     > 'if' E 'then' E
        |     > E ';' E                    right
        |     | '(' E ')'
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
        | regex {
        | Whitespaces ::= [\t\n\r\ ]*
        | Id ::= 'id'
        | }
        |
        | @Layout
        | L ::= Whitespaces
        |
        | S ::= E(0)
        | E(p) ::= [7>=p] l=E(p) [l<=0||l>=7] '.' Id     {0}
        |        | [6>=p] l=E(p) [l<=0||l>=6] r=E(7)     {6}
        |        | '-' r=E(5)                            {5}
        |        | [4>=p] l=E(p) [l<=0||l>=4] '*' r=E(5) {4}
        |        | [3>=p] l=E(p) [l<=0||l>=3] '+' r=E(4) {3}
        |        | [3>=p] l=E(p) [l<=0||l>=3] '-' r=E(4) {3}
        |        | 'if' E(0) 'then' E(2)                 {2}
        |        | [1>=p] l=E(p) [l<=0||l>=2] ';' E(0)   {1}
        |        | 'a' {0}
      """.stripMargin
    IggyParser.getRuntimeGrammar(s)
  }

  test("Parser1") { runOriginal(Input.fromString("a + if a then a + a"), 0) }
  test("Parser2") { runOriginal(Input.fromString("a;-a;a"), 0) }
  test("Parser3") { runOriginal(Input.fromString("aaa.id"), 0) }
  test("Parser4") { runOriginal(Input.fromString("a+a-a+a+-a*a"), 1) }

//  test("DDParser1") { runDesugared(Input.fromString("a + if a then a + a"), 0) }
//  test("DDParser2") { runDesugared(Input.fromString("a;-a;a"), 0) }
//  test("DDParser3") { runDesugared(Input.fromString("aaa.id"), 0) }
//  test("DDParser4") { runDesugared(Input.fromString("a+a-a+a+-a*a"), 1) }

  private def runOriginal(input: Input, amb: Int) = {
    val result = IguanaParser.parse(input, originalGrammar, start)
    assert(result.isParseSuccess)
    assertResult(amb)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

  private def runDesugared(input: Input, amb: Int) = {
    val result = IguanaParser.parse(input, desugaredGrammar, start)
    assert(result.isParseSuccess)
    assertResult(amb)(result.asParseSuccess.getStatistics.getCountAmbiguousNodes)
  }

}
