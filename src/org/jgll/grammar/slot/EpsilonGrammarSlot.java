package org.jgll.grammar.slot;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	public EpsilonGrammarSlot(NonterminalGrammarSlot nonterminal) {
		super(nonterminal);
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		int ci = node.getRightExtent();
		if (nonterminal.test(ci)) {
			TerminalNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode newNode = parser.getSPPFLookup().getNonterminalNode(nonterminal, ci, ci);
			parser.getSPPFLookup().addPackedNode(newNode, this, ci, DummyNode.getInstance(), epsilonNode);
			parser.setCurrentSPPFNode(newNode);
			parser.pop();
		}
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return "new EpsilonGrammarSlot(slot" + registry.getId(nonterminal) + ")";
	}

}
