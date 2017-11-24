//
//package org.iguana.traversal
//
//import iguana.parsetrees.term._
//import org.iguana.grammar.Grammar
//import org.iguana.grammar.symbol.Nonterminal
//import org.iguana.parser.Iguana
//import org.scalatest.FunSuite
//import org.iguana.grammar._
//import iguana.utils.input.Input
//import org.kiama.rewriting.Rewriter._
//
//class Test1 extends FunSuite {
//
//  val grammar: Grammar =
//    """
//      | E ::= E '+' E
//      |     | E '*' E
//      |     | 'a'
//      |
//      | @Layout Layout ::= Whitespace
//      |
//      | regex Whitespace ::= [\r \n \ ]*
//      |
//    """.stripMargin
//
//  test("rewrite") {
//    val result = Iguana.parse(Input.fromString("a+a*a"), grammar, Nonterminal.withName("E"))
//
////    result.asTerm match {
////      case Some(t) => {
////        TermVisualization.generateWithoutLayout(t, "/Users/afroozeh/output", "original")
////        TermVisualization.generateWithoutLayout(disambiguate(t), "/Users/afroozeh/output", "disambiguated")
////      }
////      case None => println("No terms")
////    }
//
//    def disambiguate(t: Term): Term = {
//
//      val Nt = NonterminalTermName
//      val T  = TerminalTermName
//      val L  = SeqNoLayout
//      val B  = NonterminalAmbiguityBranch
//
//      val rules = rule[Term] {
//        case a@AmbiguityTerm(Seq(B(r1, l1@L(Nt("E"), T("*"), Nt("E")), input1),
//                                 B(r2, l2@L(Nt("E"), T("+"), Nt("E")), input2))) => NonterminalTerm(r2, l2, input2)
//      }
//
//      rewrite(everywherebu(rules))(t)
//    }
//  }
//
//}
