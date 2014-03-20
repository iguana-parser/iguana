package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.regex.RegularExpression;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class TokenGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	protected final int tokenID;
	
	protected final int nodeId;
	
	private final RegularExpression regularExpression;
	
	public TokenGrammarSlot(int id, int nodeId, String label, BodyGrammarSlot previous, RegularExpression regularExpression, int tokenID) {
		super(id, label, previous);
		this.regularExpression = regularExpression;
		this.tokenID = tokenID;
		this.nodeId = nodeId;
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
