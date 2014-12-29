package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.util.Input;


public class EndTerminalGrammarSlot extends EndGrammarSlot {

	public EndTerminalGrammarSlot(Position position, NonterminalGrammarSlot nonterminal) {
		super(position, nonterminal);
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = node.getRightExtent();
		if (nonterminal.test(ci)) {
			NonterminalNode cn = parser.getSPPFLookup().getNonterminalNode(this, node);
			parser.pop(parser.getCurrentGSSNode(), ci, cn);
		}
	}

}
