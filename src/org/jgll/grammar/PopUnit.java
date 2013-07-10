package org.jgll.grammar;

import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;

public class PopUnit {

	private GSSNode gssNode;
	private int inputIndex;
	private SPPFNode sppfNode;

	public PopUnit(GSSNode gssNode, SPPFNode sppfNode, int inputIndex) {
		this.gssNode = gssNode;
		this.sppfNode = sppfNode;
		this.inputIndex = inputIndex;
	}
	
	public GSSNode getGssNode() {
		return gssNode;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}
	
	public SPPFNode getSppfNode() {
		return sppfNode;
	}
}
