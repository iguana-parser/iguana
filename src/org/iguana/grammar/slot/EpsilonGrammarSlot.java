package org.iguana.grammar.slot;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.symbol.Position;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.lookup.GSSNodeLookup;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;

import java.util.Set;

public class EpsilonGrammarSlot extends LastSymbolAndEndGrammarSlot {

	private TerminalGrammarSlot epsilonSlot;

	public EpsilonGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, TerminalGrammarSlot epsilonSlot, GSSNodeLookup nodeLookup, Set<Condition> conditions) {
		super(id, position, nonterminal, nodeLookup, null, null, conditions);
		this.epsilonSlot = epsilonSlot;
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i)) {
			TerminalNode epsilonNode = parser.getEpsilonNode(epsilonSlot, i);
			parser.pop(u, i, parser.getNonterminalNode(this, epsilonNode));
		}
	}
	
	@Override
	public String getConstructorCode() {
		return "new EpsilonGrammarSlot(slot" + nonterminal.getId() + ")";
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (nonterminal.test(i)) {
			TerminalNode epsilonNode = parser.getEpsilonNode(epsilonSlot, i);
			
			NonterminalNode nonterminalNode;
			if (u instanceof org.iguana.datadependent.gss.GSSNode<?>) {
				org.iguana.datadependent.gss.GSSNode<?> gssNode = (org.iguana.datadependent.gss.GSSNode<?>) u;
				nonterminalNode = parser.getNonterminalNode(this, epsilonNode, gssNode.getData());
			} else 
				nonterminalNode = parser.getNonterminalNode(this, epsilonNode);
			
			parser.pop(u, i, nonterminalNode);
		}
		
	}

}
