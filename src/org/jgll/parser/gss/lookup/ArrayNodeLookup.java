package org.jgll.parser.gss.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class ArrayNodeLookup extends AbstractNodeLookup {

	private GSSNode[] gssNodes;
	
	public ArrayNodeLookup(Input input) {
		gssNodes = new GSSNode[input.length()];
	}
	
	@Override
	public GSSNode getOrElseCreate(GrammarSlot slot, int i) {
		GSSNode v;
		if ((v = gssNodes[i]) == null) {
			v = new GSSNode(slot, i);
			gssNodes[i] = v;
		}
		return v;
	}

	@Override
	public GSSNode get(int i) {
		return gssNodes[i];
	}

	@Override
	public void reset(Input input) {
		gssNodes = new GSSNode[input.length()];		
	}

}
