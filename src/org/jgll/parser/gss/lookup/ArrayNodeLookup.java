package org.jgll.parser.gss.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Input;

public class ArrayNodeLookup implements NodeLookup {

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

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data) {
		// FIXME:
		return null;
	}

	@Override
	public <T> GSSNode get(int i, GSSNodeData<T> data) {
		// FIXME:
		return null;
	}

}
