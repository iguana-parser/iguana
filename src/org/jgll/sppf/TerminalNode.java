package org.jgll.sppf;

import org.jgll.regex.RegularExpression;
import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalNode extends NonPackedNode {
	
	public TerminalNode(RegularExpression slot, int leftExtent, int rightExtent) {
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
	public TerminalNode init() {
		return this;
	}
	
	@Override
	public int childrenCount() {
		return 0;
	}

}