package org.jgll.sppf;

import java.util.Collections;
import java.util.List;

import org.jgll.parser.HashFunctions;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenSymbolNode extends SPPFNode {
	
	private final int tokenID;
	
	private final int inputIndex;
	
	private final int length;
	
	public TokenSymbolNode(int tokenID, int inputIndex, int length) {
		this.tokenID = tokenID;
		this.inputIndex = inputIndex;
		this.length = length;
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
		return HashFunctions.defaulFunction.hash(tokenID, inputIndex);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", tokenID, inputIndex, getRightExtent());
	}
	
	public int getTokenID() {
		return tokenID;
	}
	
	@Override
	public int getId() {
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
	public List<SPPFNode> getChildren() {
		return Collections.emptyList();
	}
	
	@Override
	public boolean isAmbiguous() {
		return false;
	}

}