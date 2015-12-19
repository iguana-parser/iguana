package org.iguana.grammar.iggy

import java.io.File

import org.iguana.grammar.symbol.Nonterminal
import org.iguana.grammar.{GrammarGraph, Grammar}
import org.iguana.grammar.transformation.{LayoutWeaver, EBNFToBNF, DesugarPrecedenceAndAssociativity}
import org.iguana.parser.{Iguana, ParseResult}
import iguana.utils.input.Input


class IggyParser(f: String) {

  import IggyParser._

  val graph = GrammarGraph.from(iggyGrammar, Input.fromFile(new File(f)))

  def parse(input: Input): ParseResult = {
    Iguana.parse(input, graph, Nonterminal.withName("Definition"))
  }

}

object IggyParser extends App {

  private var iggyGrammar = Grammar.load(getClass.getResourceAsStream("/org/iguana/grammar/iggy/IGGY"))
  private val precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity
  precedenceAndAssociativity.setOP2

  iggyGrammar = new EBNFToBNF().transform(iggyGrammar)
  iggyGrammar = precedenceAndAssociativity.transform(iggyGrammar)
  iggyGrammar = new LayoutWeaver().transform(iggyGrammar)
}
