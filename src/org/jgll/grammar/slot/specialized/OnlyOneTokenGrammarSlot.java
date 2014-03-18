package org.jgll.grammar.slot.specialized;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.TokenSymbolNode;

/**
 * Corresponds to the case A ::= .x, where x is the only terminal
 * in the right hand side of the rule.
 * 
 * @author Ali Afroozeh
 *
 */
public class OnlyOneTokenGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	protected final int tokenID;
	
	protected final int nodeId;
	
	private final RegularExpression regularExpression;
	
	public OnlyOneTokenGrammarSlot(int id, int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, HeadGrammarSlot head, int tokenID) {
		super(id, label, previous, head);
		this.regularExpression = regularExpression;
		this.tokenID = tokenID;
		this.nodeId = nodeId;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {

		if(executePreConditions(parser, lexer)) {
			return null;
		}
		
		int ci = parser.getCurrentInputIndex();

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		TokenSymbolNode cn = parser.getTokenNode(tokenID, ci, length);
		
		// No GSS node is created for token grammar slots, therefore, pop
		// actions should be executed at this point
		if(executePopActions(parser, lexer)) {
			return null;
		}

		parser.getNonterminalNode((LastGrammarSlot) next, cn);
		parser.pop();
		return null;
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
