package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Character;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.TokenSymbolNode;

public class CharacterGrammarSlot extends TokenGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Character c;

	public CharacterGrammarSlot(int id, int nodeId, String label, BodyGrammarSlot previous,
								Character c, HeadGrammarSlot head, int tokenID) {
		super(id, nodeId, label, previous, c, head, tokenID);
		this.c = c;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		if(executePreConditions(parser, lexer)) {
			return null;
		}
		
		int ci = parser.getCurrentInputIndex();
		
		if(lexer.getInput().charAt(ci) != c.getValue()) {
			parser.recordParseError(this);
			return null;
		}
		
		TokenSymbolNode cr = parser.getTokenNode(tokenID, ci, 1);
		
		// No GSS node is created for token grammar slots, therefore, pop
		// actions should be executed at this point
		if(executePopActions(parser, lexer)) {
			return null;
		}
		
		if(next instanceof LastGrammarSlot) {
			parser.getNonterminalNode((LastGrammarSlot) next, parser.getCurrentSPPFNode(), cr);
			parser.pop();
			return null;
		} else {
			parser.getIntermediateNode(next, parser.getCurrentSPPFNode(), cr);
		}
		
		return next;
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}

}
