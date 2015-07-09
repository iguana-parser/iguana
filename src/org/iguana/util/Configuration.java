/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Configuration {
	
	public static final Configuration DEFAULT = builder().build();
	
	private final GSSType gssType;
	
	private final LookupImpl gssLookupImpl;
	
	private final LookupImpl sppfLookupImpl;
	
	private final LookupImpl descriptorLookupImpl;
	
	private final LookupStrategy gssLookupStrategy;
	
	private final LookupStrategy sppfLookupStrategy;
	
	private final LookupStrategy descriptorLookupStrategy;
	
	private final MatcherType matcherType;
	
	private final int lookAheadCount;
	
	private Configuration(Builder builder) {
		this.gssType = builder.gssType;
		this.gssLookupImpl = builder.gssLookupImpl;
		this.sppfLookupImpl = builder.sppfLookupImpl;
		this.descriptorLookupImpl = builder.descriptorLookupImpl;
		this.gssLookupStrategy = builder.gssLookupStrategy;
		this.sppfLookupStrategy = builder.sppfLookupStrategy;
		this.descriptorLookupStrategy = builder.descriptorLookupStrategy;
		this.lookAheadCount = builder.lookaheadCount;
		this.matcherType = builder.matcherType;
	}
	
	public GSSType getGSSType() {
		return gssType;
	}
	
	public LookupImpl getGSSLookupImpl() {
		return gssLookupImpl;
	}
	
	public LookupImpl getSPPFLookupImpl() {
		return sppfLookupImpl;
	}

	public LookupImpl getDescriptorLookupImpl() {
		return descriptorLookupImpl;
	}
	
	public LookupStrategy getGSSLookupStrategy() {
		return gssLookupStrategy;
	}
	
	public LookupStrategy getSPPFLookupStrategy() {
		return sppfLookupStrategy;
	}
	
	public LookupStrategy getDescriptorLookupStrategy() {
		return descriptorLookupStrategy;
	}
	
	public int getLookAheadCount() {
		return lookAheadCount;
	}
	
	public MatcherType getMatcherType() {
		return matcherType;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static enum MatcherType {
		DFA,
		JAVA_REGEX
	}
	
	public static enum GSSType {
		NEW,
		ORIGINAL
	}
	
	public static enum LookupImpl {
		ARRAY,
		HASH_MAP
	}
	
	public static enum LookupStrategy {
		GLOBAL,
		DISTRIBUTED
	}
	
	@Override
	public String toString() {
		return "GSS Type: " + gssType + "\n" +
			   "GSS Lookup Strategy: " + gssLookupStrategy + "\n" +
			   "GSS Lookup Impl: " + gssLookupImpl + "\n" +
			   "Descriptor Lookup Strategy: " + descriptorLookupStrategy + "\n" +
			   "Descriptor Lookup Impl: " + descriptorLookupImpl + "\n" +
			   "SPPF Lookup Strategy: " + sppfLookupStrategy + "\n" +
			   "SPPF Lookup Impl: " + sppfLookupImpl;
	}
	
	public static class Builder {
		
		private GSSType gssType = GSSType.NEW;
		private LookupImpl gssLookupImpl = LookupImpl.HASH_MAP;
		private LookupImpl sppfLookupImpl = LookupImpl.HASH_MAP;
		private LookupImpl descriptorLookupImpl = LookupImpl.HASH_MAP;
		private LookupStrategy gssLookupStrategy = LookupStrategy.DISTRIBUTED;
		private LookupStrategy sppfLookupStrategy = LookupStrategy.DISTRIBUTED;
		private LookupStrategy descriptorLookupStrategy = LookupStrategy.DISTRIBUTED;;
		private final MatcherType matcherType = MatcherType.JAVA_REGEX;
		private int lookaheadCount = 1;
				
		public Configuration build() {
			return new Configuration(this);
		}
		
		public Builder setGSSType(GSSType gssType) {
			this.gssType = gssType;
			return this;
		}
		
		public Builder setGSSLookupImpl(LookupImpl gssLookupImpl) {
			this.gssLookupImpl = gssLookupImpl;
			return this;
		}
		
		public Builder setSPPFLookupImpl(LookupImpl sppfLookupImpl) {
			this.sppfLookupImpl = sppfLookupImpl;
			return this;
		}
		
		public Builder setDescriptorLookupImpl(LookupImpl descriptorLookupImpl) {
			this.descriptorLookupImpl = descriptorLookupImpl;
			return this;
		}
		
		public Builder setGSSLookupStrategy(LookupStrategy gssLookupStrategy) {
			this.gssLookupStrategy = gssLookupStrategy;
			return this;
		}
		
		public Builder setSPPFLookupStrategy(LookupStrategy sppfLookupStrategy) {
			this.sppfLookupStrategy = sppfLookupStrategy;
			return this;
		}
		
		public Builder setDescriptorLookupStrategy(LookupStrategy descriptorLookupStrategy) {
			this.descriptorLookupStrategy = descriptorLookupStrategy;
			return this;
		}
		
		public Builder setLookaheadCount(int lookaheadCount) {
			this.lookaheadCount = lookaheadCount;
			return this;
		}
	}
}
