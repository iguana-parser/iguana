package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
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
	
	protected int tokenID;
	
	private RegularExpression regularExpression;
	
	public TokenGrammarSlot(int position, BodyGrammarSlot previous, RegularExpression regularExpression, HeadGrammarSlot head, int tokenID) {
		super(position, previous, head);
		this.regularExpression = regularExpression;
		this.tokenID = tokenID;
	}
	
	public TokenGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot head) {
		TokenGrammarSlot slot = new TokenGrammarSlot(this.position, previous, this.regularExpression, head, this.tokenID);
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

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		TokenSymbolNode cr = parser.getTokenNode(tokenID, ci, length);
		
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
	public SPPFNode parseLL1(GLLParser parser, GLLLexer lexer) {
		
		int ci = parser.getCurrentInputIndex();

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if(length < 0) {
			parser.recordParseError(this);
			return null;			
		}
		
		if(executePreConditions(parser, lexer)) {
			return null;
		}
		
		return parser.getTokenNode(tokenID, ci, length);
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		int ci = recognizer.getCi();
		org.jgll.recognizer.GSSNode cu = recognizer.getCu();
		
		int length = lexer.tokenLengthAt(ci, tokenID);
		
		// A::= x1
		if(previous == null && next.next() == null) {
			if(length > 0) {
				ci += length;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
		// A ::= x1...xf, f ≥ 2
		else if(previous == null && !(next.next() == null)) {
			if(length > 0) {
				ci += length;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
		// A ::= α · a β
		else {
			if(length > 0) {
				ci += length;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
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
	public boolean isNameEqual(BodyGrammarSlot slot) {
		if(this == slot) {
			return true;
		}
		
		if(!(slot instanceof TokenGrammarSlot)) {
			return false;
		}
		
		TokenGrammarSlot other = (TokenGrammarSlot) slot;
		
		return tokenID == other.tokenID;
	}

}
