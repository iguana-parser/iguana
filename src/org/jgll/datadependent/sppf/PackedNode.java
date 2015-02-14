package org.jgll.datadependent.sppf;

import org.jgll.datadependent.env.Environment;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.sppf.NonPackedNode;

public class PackedNode extends org.jgll.sppf.PackedNode {

	private Environment env;

	public PackedNode(GrammarSlot slot, int pivot, NonPackedNode parent, Environment env) {
		super(slot, pivot, parent);
		this.env = env;
	}
	
	public Environment getEnvironment() {
		return env;
	}

}
