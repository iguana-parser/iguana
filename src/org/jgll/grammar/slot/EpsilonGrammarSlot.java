package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TokenSymbolNode;

/**
 * The grammar slot representing an empty body.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	public EpsilonGrammarSlot(int id, String label, HeadGrammarSlot head) {
		super(id, label, null, head, null, null);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			TokenSymbolNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode node = parser.getSPPFLookup().getNonterminalNode(this.getHead(), ci, ci);
			parser.getSPPFLookup().addPackedNode(node, this, ci, DummyNode.getInstance(), epsilonNode);
			parser.setCurrentSPPFNode(node);
			return parser.pop();
		}

		return null;
	}
	
	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
	}

}
