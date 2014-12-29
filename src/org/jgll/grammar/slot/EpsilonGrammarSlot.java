package org.jgll.grammar.slot;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	public EpsilonGrammarSlot(Position position, NonterminalGrammarSlot nonterminal) {
		super(position, nonterminal);
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = node.getRightExtent();
		if (nonterminal.test(ci)) {
			TerminalNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode cn = parser.getSPPFLookup().getNonterminalNode(this, epsilonNode);
			parser.pop(parser.getCurrentGSSNode(), ci, cn);
		}
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new EpsilonGrammarSlot(slot" + registry.getId(nonterminal) + ")";
	}

}
