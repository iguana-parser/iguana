package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.lookup.SPPFLookup;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.IntermediateNode;
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
							ConditionTest preConditions, ConditionTest postConditions) {
		super(id, label, previous, preConditions, postConditions);
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
		
		SPPFNode node = createNode(parser, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		return next;
	}
	
	@Override
	public SPPFNode createNode(GLLParser parser, SPPFNode leftChild, SPPFNode rightChild) {
		int leftExtent = leftChild.getLeftExtent();
		int rightExtent = rightChild.getRightExtent();
		
		SPPFLookup sppfLookup = parser.getSPPFLookup();
		
		IntermediateNode newNode = sppfLookup.getIntermediateNode(next, leftExtent, rightExtent);
		
		sppfLookup.addPackedNode(newNode, next, rightChild.getLeftExtent(), leftChild, rightChild);
		
		return newNode;
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
