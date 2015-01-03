package org.jgll.sppf;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.hashing.ExternalHashEquals;

/**
 * 
 * @author Ali Afroozeh
 * 
 */
public class DummyNode extends TerminalNode {

	private DummyNode(GrammarSlot slot, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		super(slot, leftExtent, rightExtent, hashEquals);
	}

	private static DummyNode instance;
	
	public static DummyNode getInstance() {
		if(instance == null) {
			instance = new DummyNode(null, -1, -1, new ExternalHashEquals<NonPackedNode>() {

				public int hash(NonPackedNode t) {
					return 0;
				}

				@Override
				public boolean equals(NonPackedNode t1, NonPackedNode t2) {
					return false;
				}
			});
		}
		return instance;
	}
	
	@Override
	public String toString() {
		return "$";
	}

}
