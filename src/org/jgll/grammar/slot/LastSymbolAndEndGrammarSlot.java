package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.symbol.Position;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.lookup.GSSNodeLookup;
import org.jgll.sppf.NonPackedNode;

public class LastSymbolAndEndGrammarSlot extends LastSymbolGrammarSlot {

	public LastSymbolAndEndGrammarSlot(int id, Position position, NonterminalGrammarSlot nonterminal, GSSNodeLookup nodeLookup,
			String label, String variable, Set<Condition> conditions) {
		super(id, position, nonterminal, nodeLookup, label, variable, conditions);
	}
	
	@Override
	public boolean isEnd() {
		return true;
	}
	
	@Override
	public String getConstructorCode() {
		return null;
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		if (nonterminal.test(i)) {
			parser.pop(u, i, node);
		}
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		if (nonterminal.test(i)) {
			parser.pop(u, i, node);
		}
	}

}
