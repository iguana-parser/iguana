package org.jgll.parser.gss.lookup;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class ArrayNodeLookup extends AbstractNodeLookup {

	private GSSNode[] gssNodes;
	private int length;
	
	public ArrayNodeLookup(Input input) {
		length = input.length();
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
		super.reset(input);
		length = input.length();
		gssNodes = null;		
	}
	
	@Override
	public Iterable<GSSNode> getNodes() {
		return Arrays.stream(gssNodes).filter(n -> n != null).collect(Collectors.toList());
	}

	@Override
	public ArrayNodeLookup init() {
		super.init();
		gssNodes = new GSSNode[length];
		return this;
	}

	@Override
	public boolean isInitialized() {
		return gssNodes != null;
	}

}
