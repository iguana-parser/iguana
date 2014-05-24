package org.jgll.util;

public class ParseStatistics {

	private final long nanoTime;
	private final long systemTime;
	private final long userTime;
	private final int memoryUsed;
	
	private final int descriptorsCount;
	private final int gssNodesCount;
	private final int gssEdgesCount;
	private final int nonterminalNodesCount;
	private final int intermediateNodesCount;
	private final int packedNodesCount;
	
	private final int ambiguitiesCount;
	
	private final Input input;
	
	public ParseStatistics(Input input, long nanoTime, long userTime, long systemTime,
						   int memoryUsed, int descriptorsCount, int gssNodesCount,
						   int gssEdgesCount, int nonterminalNodesCount,
						   int intermediateNodesCount, int packedNodesCount,
						   int ambiguitiesCount) {
		this.input = input;
		this.nanoTime = nanoTime;
		this.systemTime = systemTime;
		this.userTime = userTime;
		this.memoryUsed = memoryUsed;
		this.descriptorsCount = descriptorsCount;
		this.gssNodesCount = gssNodesCount;
		this.gssEdgesCount = gssEdgesCount;
		this.nonterminalNodesCount = nonterminalNodesCount;
		this.intermediateNodesCount = intermediateNodesCount;
		this.packedNodesCount = packedNodesCount;
		this.ambiguitiesCount = ambiguitiesCount;
	}

	public long getNanoTime() {
		return nanoTime;
	}
	
	public long getSystemTime() {
		return systemTime;
	}
	
	public long getUserTime() {
		return userTime;
	}
	
	public int getMemoryUsed() {
		return memoryUsed;
	}
	
	public int getDescriptorsCount() {
		return descriptorsCount;
	}
	
	public int getGssNodesCount() {
		return gssNodesCount;
	}
	
	public int getGssEdgesCount() {
		return gssEdgesCount;
	}
	
	public int getNonterminalNodesCount() {
		return nonterminalNodesCount;
	}
	
	public int getIntermediateNodesCount() {
		return intermediateNodesCount;
	}
	
	public int getPackedNodesCount() {
		return packedNodesCount;
	}
	
	public int getAmbiguitiesCount() {
		return ambiguitiesCount;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("Input size: %d, loc: %d"), input.length(), input.getLineCount());
		sb.append("Parsing Time (nano time): ").append(nanoTime).append(" ms");
		sb.append("Parsing Time (user time): ").append(userTime / 1000_000).append(" ms");
		sb.append("Parsing Time (system time): ").append(systemTime / 1000_000).append(" ms");
		sb.append("Memory used: ").append(memoryUsed).append(" mb");
		sb.append("Descriptors: ").append(descriptorsCount);
		sb.append("GSS Nodes: ").append(gssNodesCount);
		sb.append("GSS Edges: ").append(gssEdgesCount);
		sb.append("Nonterminal nodes: ").append(nonterminalNodesCount);
		sb.append("Intermediate nodes: ").append(intermediateNodesCount);
		sb.append("Packed nodes: ").append(packedNodesCount);
		sb.append("Ambiguities: ").append(ambiguitiesCount);
			
		return sb.toString();
	}
}