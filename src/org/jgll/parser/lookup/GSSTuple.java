package org.jgll.parser.lookup;

import java.util.ArrayList;
import java.util.List;

import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;

public class GSSTuple {

	private final GSSNode gssNode;
	private final List<NonPackedNode> nonPackedNodes;
	private final IguanaSet<GSSEdge> gssEdges;

	public GSSTuple(GSSNode gssNode) {
		this.gssNode = gssNode;
		nonPackedNodes = new ArrayList<>();
		gssEdges = HashTableFactory.getFactory().newHashSet(GSSEdge.externalHasher);
	}
	
	public GSSNode getGssNode() {
		return gssNode;
	}
	
	public void addNonPackedNode(NonPackedNode node) {
		nonPackedNodes.add(node);
	}
	
	public List<NonPackedNode> getNonPackedNodes() {
		return nonPackedNodes;
	}
	
	public IguanaSet<GSSEdge> getGssEdges() {
		return gssEdges;
	}
	
	public void addGSSEdge(GSSEdge edge) {
		gssEdges.add(edge);
	}
	
}
