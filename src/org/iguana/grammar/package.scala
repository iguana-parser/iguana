package org.iguana

import iguana.utils.input.Input
import org.iguana.grammar.iggy.IggyParser

package object grammar {

  implicit def toGrammar(s: String) = IggyParser.getGrammar(Input.fromString(s))

}
