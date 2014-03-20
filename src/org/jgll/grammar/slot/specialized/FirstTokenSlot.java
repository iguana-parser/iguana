package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.TokenSymbolNode;

public class FirstTokenSlot extends TokenGrammarSlot {

	private static final long serialVersionUID = 1L;

	public FirstTokenSlot(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression, int tokenID) {
		super(id, nodeId, label, previous, regularExpression, tokenID);
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		if(preConditions.execute(parser, lexer, ci)) {
			return null;
		}

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		if(postConditions.execute(parser, lexer, ci + length)) {
			return null;
		}
		
		TokenSymbolNode cn = parser.getTokenNode(tokenID, ci, length);
		parser.setCurrentSPPFNode(cn);

		return next;
	}

	@Override
	public ConditionTest getPreConditions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ConditionTest getPostConditions() {
		throw new UnsupportedOperationException();
	}

}
