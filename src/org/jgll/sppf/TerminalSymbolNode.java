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
public class TerminalSymbolNode extends SPPFNode {
	
	public static final ExternalHasher<TerminalSymbolNode> externalHasher = new TerminalSymbolNodeExternalHasher();
	
	public static final int EPSILON = -2;
	
	private final int matchedChar;
	
	private final int inputIndex;
	
	public TerminalSymbolNode(int matchedChar, int inputIndex) {
		this.matchedChar = matchedChar;
		this.inputIndex = inputIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof TerminalSymbolNode)) {
			return false;
		}
		
		TerminalSymbolNode other = (TerminalSymbolNode) obj;
		
		return matchedChar == other.matchedChar &&
			   inputIndex == other.inputIndex;
	}
	
	@Override
	public int hashCode() {
		return externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	@Override
	public String getLabel() {
		return matchedChar == EPSILON ? "\u03B5" : (char) matchedChar + "";
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), inputIndex, getRightExtent());
	}
	
	public int getMatchedChar() {
		return matchedChar;
	}
	
	@Override
	public int getLeftExtent() {
		return inputIndex;
	}

	@Override
	public int getRightExtent() {
		return matchedChar == EPSILON ? inputIndex : inputIndex + 1;
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

	@Override
	public int getLevel() {
		return inputIndex;
	}
	
	public static class TerminalSymbolNodeExternalHasher implements ExternalHasher<TerminalSymbolNode> {

		private static final long serialVersionUID = 1L;

		@Override
		public int hash(TerminalSymbolNode t, HashFunction f) {
			return f.hash(t.inputIndex, t.matchedChar);
		}

		@Override
		public boolean equals(TerminalSymbolNode t1, TerminalSymbolNode t2) {
			return t1.matchedChar == t2.matchedChar &&
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