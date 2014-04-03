package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	protected final int tokenID;
	
	protected final int nodeId;
	
	private final RegularExpression regularExpression;
	
	public TokenGrammarSlot(int id, int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, int tokenID,
							ConditionTest preConditions, ConditionTest postConditions, ConditionTest popConditions,
							NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, preConditions, postConditions, popConditions, nodeCreator, nodeCreatorFromPop);
		this.regularExpression = regularExpression;
		this.tokenID = tokenID;
		this.nodeId = nodeId;
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
		
		return next;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNullable() {
		return regularExpression.isNullable();
	}
	
	public int getTokenID() {
		return tokenID;
	}
	
	@Override
	public RegularExpression getSymbol() {
		return regularExpression;
	}
	
	@Override
	public int getNodeId() {
		return nodeId;
	}

}
