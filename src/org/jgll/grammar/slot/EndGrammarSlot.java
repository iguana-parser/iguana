package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

public class EndGrammarSlot implements GrammarSlot {
	
	protected final List<NonterminalGrammarSlot> nonterminals;

	public EndGrammarSlot(List<NonterminalGrammarSlot> nonterminals) {
		this.nonterminals = nonterminals;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		nonterminals.stream().filter(n -> n.test(node.getRightExtent())).forEach(n -> parser.pop());
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}
	
}
