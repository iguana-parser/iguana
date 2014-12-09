package org.jgll.sppf;

import java.util.Collections;
import java.util.List;

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

	@Override
	public RegularExpression getGrammarSlot() {
		return (RegularExpression) slot;
	}

	@Override
	public NonPackedNode init() {
		return null;
	}

}