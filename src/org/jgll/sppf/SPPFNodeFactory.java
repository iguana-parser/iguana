package org.jgll.sppf;

import java.util.List;

import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.regex.RegularExpression;

public class SPPFNodeFactory {

	private GrammarGraph grammarGraph;

	public SPPFNodeFactory(GrammarGraph grammarGraph) {
		this.grammarGraph = grammarGraph;
	}
	
	public NonterminalNode createNonterminalNode(Nonterminal nonterminal, int leftExtent, int rightExtent) {
		return new NonterminalNode(grammarGraph.getNonterminalId(nonterminal), 
										 grammarGraph.getCountAlternates(nonterminal), 
										 leftExtent, 
										 rightExtent);
	}

	public IntermediateNode createIntermediateNode(List<? extends Symbol> symbols, int leftExtent, int rightExtent) {
		int id = grammarGraph.getIntermediateNodeId(symbols);
		return new IntermediateNode(id, leftExtent, rightExtent);
	}
	
	public TokenSymbolNode createTokenNode(RegularExpression regex, int leftExtent, int rightExtent) {
		return new TokenSymbolNode(grammarGraph.getRegularExpressionId(regex), leftExtent, rightExtent - leftExtent);
	}
	
	public ListSymbolNode createListNode(Nonterminal nonterminal, int leftExtent, int rightExtent) {
		return new ListSymbolNode(grammarGraph.getNonterminalId(nonterminal), 
				 grammarGraph.getCountAlternates(nonterminal), 
				 leftExtent, 
				 rightExtent);
	}
	
	public PackedNode createPackedNode(List<? extends Symbol> symbols, int pivot, NonPackedNode parent) {
		return null;
	}
	
}
