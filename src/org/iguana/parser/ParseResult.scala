
package org.iguana.parser

import iguana.parsetrees.sppf.NonterminalNode
import iguana.parsetrees.term.{TermBuilder, DefaultTermBuilder, SPPFToTerms, Term}
import iguana.utils.input.Input
import org.iguana.grammar.slot.GrammarSlot
import org.iguana.parser.gss.GSSNode
import org.iguana.util.ParseStatistics

trait ParseResult {

  def isParseError: Boolean

  def isParseSuccess: Boolean = !isParseError

  def asParseError: ParseError

  def asParseSuccess: ParseSuccess

  def getInput: Input
}

case class ParseSuccess(sppfNode: NonterminalNode, statistics: ParseStatistics, input: Input) extends ParseResult {

  def getTreeWithoutSharing: Term = SPPFToTerms.convertNoSharing(sppfNode, new DefaultTermBuilder(input))

  def getTree: Term = SPPFToTerms.convert(sppfNode, new DefaultTermBuilder(input))

  def getTree[T >: Any](builder: TermBuilder[T]): T = SPPFToTerms.convert(sppfNode, builder)

  override def isParseError: Boolean = false

  override def getInput: Input = input

  override def asParseError: ParseError = throw new RuntimeException("Cannot call getParseError on Success.")

  override def asParseSuccess: ParseSuccess = this

  def getStatistics() = statistics

  def getSPPFNode() = sppfNode

  override def equals(that: Any) = that match {
    case ParseSuccess(sppfNode, statistics, input) => sppfNode.deepEquals(this.sppfNode) &&
                                                      statistics == this.statistics &&
                                                      input == this.input
    case ParseError                                => false
  }

}

case class ParseError (slot: GrammarSlot, input: Input, inputIndex: Int, gssNode: GSSNode) extends  ParseResult {

  override def isParseError: Boolean = true

  override def getInput: Input = input

  override def asParseError: ParseError = this

  override def asParseSuccess: ParseSuccess = throw new RuntimeException("Cannot call getParseSuccess on ParseError.")

  override def toString = s"Parse error at ${inputIndex}, line: ${input.getLineNumber(inputIndex)}, column: ${input.getColumnNumber(inputIndex)}"
}

//case class ParseStatistics(nanoTime: Long = 0,
//                           systemTime: Long = 0,
//                           userTime: Long = 0,
//                           memoryUsed: Long,
//                           descriptorsCount: Int = 0,
//                           gssNodesCount: Int = 0,
//                           gssEdgesCount: Int = 0,
//                           nonterminalNodesCount: Int = 0,
//                           terminalNodesCount: Int = 0,
//                           intermediateNodesCount: Int = 0,
//                           packedNodesCount: Int = 0,
//                           ambiguousNodesCount: Int = 0)