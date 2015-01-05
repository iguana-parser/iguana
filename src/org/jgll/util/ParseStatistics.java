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
	private final int terminalNodesCount;
	private final int intermediateNodesCount;
	private final int packedNodesCount;
	private final int ambiguousNodesCount;
	
	private final Input input;
	
	public ParseStatistics(Input input, long nanoTime, long userTime, long systemTime,
						   int memoryUsed, int descriptorsCount, int gssNodesCount,
						   int gssEdgesCount, int nonterminalNodesCount, int terminalNodesCount,
						   int intermediateNodesCount, int packedNodesCount,
						   int ambiguousNodesCount) {
		this.input = input;
		this.nanoTime = nanoTime;
		this.systemTime = systemTime;
		this.userTime = userTime;
		this.memoryUsed = memoryUsed;
		this.descriptorsCount = descriptorsCount;
		this.gssNodesCount = gssNodesCount;
		this.gssEdgesCount = gssEdgesCount;
		this.nonterminalNodesCount = nonterminalNodesCount;
		this.terminalNodesCount = terminalNodesCount;
		this.intermediateNodesCount = intermediateNodesCount;
		this.packedNodesCount = packedNodesCount;
		this.ambiguousNodesCount = ambiguousNodesCount;
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
	
	public int getTerminalNodesCount() {
		return terminalNodesCount;
	}
	
	public int getIntermediateNodesCount() {
		return intermediateNodesCount;
	}
	
	public int getPackedNodesCount() {
		return packedNodesCount;
	}
	
	public int getCountAmbiguousNodes() {
		return ambiguousNodesCount;
	}
	
	public Input getInput() {
		return input;
	}
	
	@Override
	public String toString() {
		return  "Input size: " + input.length() + ", " + input.getLineCount() + "\n" +
				"Parsing Time (nano time): " + nanoTime / 1000_000 + " ms" + "\n" +
				"Parsing Time (user time): " + userTime / 1000_000 + " ms" + "\n" +
				"Parsing Time (system time): " + systemTime / 1000_000 + " ms" + "\n" +
				"Memory used: " + memoryUsed + " mb" + "\n" +
				"Descriptors: " + descriptorsCount + "\n" +
				"GSS Nodes: " + gssNodesCount + "\n" +
				"GSS Edges: " + gssEdgesCount + "\n" +
				"Nonterminal nodes: " + nonterminalNodesCount + "\n" +
				"Intermediate nodes: " + intermediateNodesCount + "\n" +
				"Packed nodes: " + packedNodesCount + "\n" +
				"Ambiguities: " + ambiguousNodesCount + "\n";
	}
}