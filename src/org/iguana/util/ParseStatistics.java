/*
 * Copyright (c) 2015, CWI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY 
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.util;

import org.iguana.parser.HashFunctions;


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
	
	public ParseStatistics(Builder builder) {
		this.nanoTime = builder.nanoTime;
		this.systemTime = builder.systemTime;
		this.userTime = builder.userTime;
		this.memoryUsed = builder.memoryUsed;
		this.descriptorsCount = builder.descriptorsCount;
		this.gssNodesCount = builder.gssNodesCount;
		this.gssEdgesCount = builder.gssEdgesCount;
		this.nonterminalNodesCount = builder.nonterminalNodesCount;
		this.terminalNodesCount = builder.terminalNodesCount;
		this.intermediateNodesCount = builder.intermediateNodesCount;
		this.packedNodesCount = builder.packedNodesCount;
		this.ambiguousNodesCount = builder.ambiguousNodesCount;
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(descriptorsCount,
				gssNodesCount, gssEdgesCount, nonterminalNodesCount,
				terminalNodesCount, intermediateNodesCount, packedNodesCount,
				ambiguousNodesCount);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof ParseStatistics)) 
			return false;
		
		ParseStatistics other = (ParseStatistics) obj;
		
		return descriptorsCount == other.descriptorsCount &&
			   gssNodesCount == other.gssNodesCount &&
			   gssEdgesCount == other.gssEdgesCount &&
			   nonterminalNodesCount == other.nonterminalNodesCount &&
			   terminalNodesCount == other.terminalNodesCount &&
			   intermediateNodesCount == other.intermediateNodesCount &&
			   packedNodesCount == other.packedNodesCount &&
			   ambiguousNodesCount == other.ambiguousNodesCount;
	}
	
	@Override
	public String toString() {
		return  "Parsing Time (nano time): " + nanoTime / 1000_000 + " ms" + "\n" +
				"Parsing Time (user time): " + userTime / 1000_000 + " ms" + "\n" +
				"Parsing Time (system time): " + systemTime / 1000_000 + " ms" + "\n" +
				"Memory used: " + memoryUsed + " mb" + "\n" +
				"Descriptors: " + descriptorsCount + "\n" +
				"GSS Nodes: " + gssNodesCount + "\n" +
				"GSS Edges: " + gssEdgesCount + "\n" +
				"Nonterminal nodes: " + nonterminalNodesCount + "\n" +
				"Terminal nodes: " + terminalNodesCount + "\n" +
				"Intermediate nodes: " + intermediateNodesCount + "\n" +
				"Packed nodes: " + packedNodesCount + "\n" +
				"Ambiguities: " + ambiguousNodesCount + "\n";
	}
	
	public static class Builder {
		long nanoTime;
		long systemTime;
		long userTime;
		int memoryUsed;
		
		int descriptorsCount;
		int gssNodesCount;
		int gssEdgesCount;
		int nonterminalNodesCount;
		int terminalNodesCount;
		int intermediateNodesCount;
		int packedNodesCount;
		int ambiguousNodesCount;
		
		public Builder setNanoTime(long nanoTime) {
			this.nanoTime = nanoTime;
			return this;
		}
		
		public Builder setSystemTime(long systemTime) {
			this.systemTime = systemTime;
			return this;
		}

		public Builder setUserTime(long userTime) {
			this.userTime = userTime;
			return this;
		}

		public Builder setMemoryUsed(int memoryUsed) {
			this.memoryUsed = memoryUsed;
			return this;
		}

		public Builder setDescriptorsCount(int descriptorsCount) {
			this.descriptorsCount = descriptorsCount;
			return this;
		}

		public Builder setGSSNodesCount(int gssNodesCount) {
			this.gssNodesCount = gssNodesCount;
			return this;
		}

		public Builder setGSSEdgesCount(int gssEdgesCount) {
			this.gssEdgesCount = gssEdgesCount;
			return this;
		}

		public Builder setNonterminalNodesCount(int nonterminalNodesCount) {
			this.nonterminalNodesCount = nonterminalNodesCount;
			return this;
		}

		public Builder setTerminalNodesCount(int terminalNodesCount) {
			this.terminalNodesCount = terminalNodesCount;
			return this;
		}

		public Builder setIntermediateNodesCount(int intermediateNodesCount) {
			this.intermediateNodesCount = intermediateNodesCount;
			return this;
		}

		public Builder setPackedNodesCount(int packedNodesCount) {
			this.packedNodesCount = packedNodesCount;
			return this;
		}

		public Builder setAmbiguousNodesCount(int ambiguousNodesCount) {
			this.ambiguousNodesCount = ambiguousNodesCount;
			return this;
		}
		
		public ParseStatistics build() {
			return new ParseStatistics(this);
		}

	}
}