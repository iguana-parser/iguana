package org.jgll.sppf;

import java.util.Collections;
import java.util.List;

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
	public int childrenCount() {
		return 0;
	}
	
	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public SPPFNode getChildAt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SPPFNode> getChildren() {
		return Collections.emptyList();
	}
	
}