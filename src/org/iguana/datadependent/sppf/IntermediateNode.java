package org.jgll.datadependent.sppf;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.sppf.PackedNodeSet;

public class IntermediateNode extends org.jgll.sppf.IntermediateNode {

	private Environment env;

	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set, Environment env) {
		super(slot, leftExtent, rightExtent, set);
		this.env = env;
	}
	
	public Environment getEnvironment() {
		return env;
	}
}
