package org.jgll.parser.lookup;

import java.util.ArrayList;
import java.util.List;

import org.jgll.parser.GSSEdge;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;

public class GSSTuple {

	private GSSNode gssNode;
	private List<NonPackedNode> nonPackedNodes;
	private IguanaSet<GSSEdge> gssEdges;

	public GSSTuple() {
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
