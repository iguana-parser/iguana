package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public class LastTokenSlot extends TokenGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public LastTokenSlot(int id, int nodeId, String label,
 						 BodyGrammarSlot previous, RegularExpression regularExpression,
						 int tokenID, ConditionTest preConditions,
						 ConditionTest postConditions, NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {
		
		super(id, nodeId, label, previous, regularExpression,
			  tokenID, preConditions, postConditions, nodeCreator, nodeCreatorFromPop);
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
		
		TokenSymbolNode cr = parser.getTokenNode(tokenID, ci, length);
		
		SPPFNode node = nodeCreator.create(parser, next, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		parser.pop();
		
		return null;
	}
}
