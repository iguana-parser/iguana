package org.jgll.sppf;

import java.util.Collections;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenSymbolNode extends SPPFNode {
	
	public static final ExternalHasher<TokenSymbolNode> externalHasher = new TerminalSymbolNodeExternalHasher();
	
	public static final int EPSILON = -2;
	
	private final int tokenID;
	
	private final int inputIndex;
	
	private final int length;
	
	private final int hash;
	
	public TokenSymbolNode(int tokenID, int inputIndex, int length) {
		this.tokenID = tokenID;
		this.inputIndex = inputIndex;
		this.length = length;
		this.hash = externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		TokenSymbolNode other = (TokenSymbolNode) obj;
		
		return tokenID == other.tokenID &&
			   inputIndex == other.inputIndex;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String getLabel() {
		return String.format("(%s, %d, %d)", tokenID, inputIndex, getRightExtent());
	}
	
	@Override
	public String toString() {
		return getLabel();
	}
	
	public int getTokenID() {
		return tokenID;
	}
	
	@Override
	public int getLeftExtent() {
		return inputIndex;
	}

	@Override
	public int getRightExtent() {
		return inputIndex + length;
	}
	
	public int getLength() {
		return length;
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

	@Override
	public SPPFNode getChildAt(int index) {
		return null;
	}

	@Override
	public int childrenCount() {
		return 0;
	}

	@Override
	public Iterable<SPPFNode> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public GrammarSlot getGrammarSlot() {
		throw new UnsupportedOperationException();
	}

	public static class TerminalSymbolNodeExternalHasher implements ExternalHasher<TokenSymbolNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(TokenSymbolNode t, HashFunction f) {
			return f.hash(t.inputIndex, t.tokenID);
		}

		@Override
		public boolean equals(TokenSymbolNode t1, TokenSymbolNode t2) {
			return t1.tokenID == t2.tokenID &&
				   t1.inputIndex == t2.inputIndex;

		}
	}

	@Override
	public SPPFNode getLastChild() {
		return null;
	}

	@Override
	public SPPFNode getFirstChild() {
		return null;
	}

}