package org.iguana.disambiguation.conditions

import iguana.regex.Character
import iguana.utils.input.Input
import org.iguana.grammar.Grammar
import org.iguana.grammar.condition.RegularExpressionCondition
import org.iguana.grammar.symbol.{Nonterminal, Rule, Terminal}
import org.iguana.parser.{Iguana, ParseResult}
import org.junit.Assert._

class DanglingElse1 extends FunSuite {

  val S = Nonterminal.withName("S")
  val s = Terminal.from(Character.from('s'))
  val a = Terminal.from(Character.from('a'))
  var b = Terminal.from(Character.from('b'))

  val grammar = {
    val builder: Grammar.Builder = new Grammar.Builder
    val rule1: Rule = Rule.withHead(S).addSymbols(a, Nonterminal.builder("S").addPreCondition(RegularExpressionCondition.notFollow(Character.from('b'))).build).build
    builder.addRule(rule1)
    val rule2: Rule = Rule.withHead(S).addSymbols(a, S, b, S).build
    builder.addRule(rule2)
    val rule3: Rule = Rule.withHead(S).addSymbols(s).build
    builder.addRule(rule3)
    builder.build
  }

  test("Parser") {
    val input: Input = Input.fromString("aasbs")
    val result: ParseResult = Iguana.parse(input, grammar, Nonterminal.withName("S"))
    assertTrue(result.isParseSuccess)
//    println(TermToScalaCode.get(result.asParseSuccess.getTerm))
  }

}
