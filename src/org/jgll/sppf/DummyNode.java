package org.jgll.sppf;

import org.jgll.grammar.slot.DummySlot;
import org.jgll.grammar.slot.GrammarSlot;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public class DummyNode extends TerminalNode {

	private DummyNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		super(slot, leftExtent, rightExtent);
	}

	private static DummyNode instance;
	
	public static DummyNode getInstance() {
		if(instance == null) {
			instance = new DummyNode(DummySlot.getInstance(), -1, -1);
		}
		return instance;
	}
	
	@Override
	public String toString() {
		return "$";
	}

}
