package org.iguana.datadependent.sppf;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.sppf.PackedNodeSet;

public class IntermediateNode extends org.iguana.sppf.IntermediateNode {

	private Environment env;

	public IntermediateNode(GrammarSlot slot, int leftExtent, int rightExtent, PackedNodeSet set, Environment env) {
		super(slot, leftExtent, rightExtent, set);
		this.env = env;
	}
	
	public Environment getEnvironment() {
		return env;
	}
}
