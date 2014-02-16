package org.jgll.parser.lookup;

import org.jgll.parser.gss.GSSEdge;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.hashing.HashTableFactory;
import org.jgll.util.hashing.IguanaSet;

public class GSSTuple {

	private final GSSNode gssNode;
	private final IguanaSet<GSSEdge> gssEdges;

	public GSSTuple(GSSNode gssNode) {
		this.gssNode = gssNode;
		gssEdges = HashTableFactory.getFactory().newHashSet(GSSEdge.externalHasher);
	}
	
	public GSSNode getGssNode() {
		return gssNode;
	}
		
	public IguanaSet<GSSEdge> getGssEdges() {
		return gssEdges;
	}
	
	public void addGSSEdge(GSSEdge edge) {
		gssEdges.add(edge);
	}
	
}
