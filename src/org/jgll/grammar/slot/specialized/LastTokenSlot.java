package org.jgll.grammar.slot.specialized;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.grammar.slot.TokenGrammarSlot;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

public class LastTokenSlot extends TokenGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public LastTokenSlot(int id, int nodeId, String label,
 						 BodyGrammarSlot previous, RegularExpression regularExpression,
						 int tokenID, ConditionTest preConditions,
						 ConditionTest postConditions) {
		
		super(id, nodeId, label, previous, regularExpression, tokenID, preConditions, postConditions);
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
		
		SPPFNode node = createNode(parser, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		parser.pop();
		
		return null;
	}

	@Override
	public SPPFNode createNode(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		int leftExtent = leftChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		LastGrammarSlot last = (LastGrammarSlot) next;
		NonterminalSymbolNode newNode = sppfLookup.getNonterminalNode(last.getHead(), leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, last, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
	}
}
