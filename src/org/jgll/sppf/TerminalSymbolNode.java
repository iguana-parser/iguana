package org.jgll.sppf;

import java.util.Collections;

import org.jgll.regex.RegularExpression;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalSymbolNode extends NonPackedNode {
	
	public TerminalSymbolNode(RegularExpression slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}
	
	@Override
	public void accept(SPPFVisitor visitAction) {
		visitAction.visit(this);
	}

	@Override
	public RegularExpression getGrammarSlot() {
		return (RegularExpression) slot;
	}

	@Override
	public TerminalSymbolNode init() {
		children = Collections.emptyList();
		return this;
	}

}