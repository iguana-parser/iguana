package org.jgll.parser.gss.lookup;

public abstract class AbstractGSSLookup implements GSSLookup {

	protected int countGSSEdges;
	
	protected int countGSSNodes;

	@Override
	public int getGSSNodesCount() {
		return countGSSNodes;
	}

	@Override
	public int getGSSEdgesCount() {
		return countGSSEdges;
	}
	
	@Override
	public void reset() {
		countGSSEdges = 0;
		countGSSNodes = 0;
	}
}
