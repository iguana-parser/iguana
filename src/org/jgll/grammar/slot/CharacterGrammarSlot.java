package org.jgll.grammar.slot;

import org.jgll.grammar.symbol.Character;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.TokenSymbolNode;

public class CharacterGrammarSlot extends TokenGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private Character c;

	public CharacterGrammarSlot(int slotId, String label, BodyGrammarSlot previous,
								Character c, HeadGrammarSlot head, int tokenID) {
		super(slotId, label, previous, c, head, tokenID);
		this.c = c;
	}
	
	public CharacterGrammarSlot copy(BodyGrammarSlot previous, String label, HeadGrammarSlot head) {
		CharacterGrammarSlot slot = new CharacterGrammarSlot(slotId, label, previous, this.c, head, this.tokenID);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
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
			parser.getNonterminalNode((LastGrammarSlot) next, cr);
			parser.pop();
			return null;
		} else {
			parser.getIntermediateNode(next, cr);
		}
		
		return next;
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}

}
