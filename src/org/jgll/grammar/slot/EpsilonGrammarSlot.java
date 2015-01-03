package org.jgll.grammar.slot;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	public EpsilonGrammarSlot(Position position, NonterminalGrammarSlot nonterminal, NodeLookup nodeLookup) {
		super(position, nonterminal, nodeLookup);
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i)) {
			TerminalNode epsilonNode = parser.getEpsilonNode(i);
			parser.pop(u, i, parser.getNonterminalNode(this, epsilonNode));
		}
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new EpsilonGrammarSlot(slot" + registry.getId(nonterminal) + ")";
	}

}
