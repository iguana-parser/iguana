package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public class LastTokenSlot extends TokenGrammarSlot {

	public LastTokenSlot(int id, String label,
 						 BodyGrammarSlot previous, RegularExpression regularExpression,
						 int tokenID, ConditionTest preConditions,
						 ConditionTest postConditions, ConditionTest popConditions,
						 NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {
		
		super(id, label, previous, regularExpression, tokenID, preConditions, 
			  postConditions, popConditions, nodeCreator, nodeCreatorFromPop);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		if(preConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci)) {
			return null;
		}

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		if(postConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci + length)) {
			return null;
		}
		
		TokenSymbolNode cr = parser.getTokenNode(tokenID, ci, length);
		
		SPPFNode node = nodeCreator.create(parser, next, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		return parser.pop();
	}
}
