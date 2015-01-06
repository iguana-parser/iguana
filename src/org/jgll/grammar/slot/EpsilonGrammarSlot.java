package org.jgll.grammar.slot;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.NodeLookup;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	public EpsilonGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, NodeLookup nodeLookup) {
		super(id, position, nonterminal, nodeLookup);
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		// FIXME: Data-dependent GLL
		if (nonterminal.test(i)) {
			TerminalNode epsilonNode = parser.getEpsilonNode(i);
			parser.pop(u, i, parser.getNonterminalNode(this, epsilonNode));
		}
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return "new EpsilonGrammarSlot(slot" + registry.getId(nonterminal) + ")";
	}

}
