package org.jgll.sppf;

import java.util.Collections;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.TerminalGrammarSlot;
import org.jgll.parser.HashFunctions;
import org.jgll.regex.RegularExpression;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenSymbolNode extends SPPFNode {
	
	private final TerminalGrammarSlot slot;
	
	private final int inputIndex;
	
	private final int length;
	
	public TokenSymbolNode(TerminalGrammarSlot slot, int inputIndex, int length) {
		this.slot = slot;
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
		
		return slot == other.slot &&
			   inputIndex == other.inputIndex;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), inputIndex);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot.getRegularExpression(), inputIndex, getRightExtent());
	}
	
	@Override
	public GrammarSlot getGrammarSlot() {
		return slot;
	}
	
	public RegularExpression getRegularExpression() {
		return slot.getRegularExpression();
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