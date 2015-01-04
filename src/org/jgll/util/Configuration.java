package org.jgll.util;

import org.jgll.grammar.Grammar;
import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class Configuration {
	
	private final GSSType gssType;
	
	private final HashFunction sppfHashFunction;
	
	private final HashFunction descriptorHashFunction;
	
	private final LookupImpl gssLookupImpl;
	
	private final LookupImpl sppfLookupImpl;
	
	private LookupStrategy gssLookupStrategy;
	
	private LookupStrategy sppfLookupStrategy;
	
	private Configuration(Builder builder) {
		this.gssType = builder.gssType;
		this.sppfHashFunction = builder.sppfHashFunction;
		this.descriptorHashFunction = builder.descriptorHashFunction;
		this.gssLookupImpl = builder.gssLookupImpl;
		this.sppfLookupImpl = builder.sppfLookupImpl;
		this.gssLookupStrategy = builder.gssLookupStrategy;
		this.sppfLookupStrategy = builder.sppfLookupStrategy;
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
	
	public LookupStrategy getGSSLookupStrategy() {
		return gssLookupStrategy;
	}
	
	public LookupStrategy getSPPFLookupStrategy() {
		return sppfLookupStrategy;
	}
	
	public HashFunction getSppfHashFunction() {
		return sppfHashFunction;
	}
	
	public HashFunction getDescriptorHashFunction() {
		return descriptorHashFunction;
	}
	
	public static Builder builder(Grammar grammar, Input input) {
		return new Builder(grammar, input);
	}
	
	public static class Builder {
		
		private GSSType gssType;
		private LookupImpl gssLookupImpl;
		private LookupImpl sppfLookupImpl;
		private LookupStrategy gssLookupStrategy;
		private LookupStrategy sppfLookupStrategy;
		
		private HashFunction sppfHashFunction;
		private HashFunction descriptorHashFunction;
		
		public Builder(Grammar grammar, Input input) {
			this.gssType = GSSType.NEW;
			this.gssLookupImpl = LookupImpl.ARRAY;
			this.sppfLookupImpl = LookupImpl.HASH_MAP;
			this.gssLookupStrategy = LookupStrategy.DISTRIBUTED;
			this.sppfLookupStrategy = LookupStrategy.DISTRIBUTED;
			
			this.sppfHashFunction = HashFunctions.coefficientHash(input.length(), input.length(), grammar.size());
			this.descriptorHashFunction = HashFunctions.coefficientHash(grammar.size(), input.length(), grammar.size(), input.length());
		}
		
		public Configuration build() {
			return new Configuration(this);
		}
		
		public Builder setGSSType(GSSType gssType) {
			this.gssType = gssType;
			return this;
		}

		public void setSPPFHashFunction(HashFunction sppfHashFunction) {
			this.sppfHashFunction = sppfHashFunction;
		}

		public void setDescriptorHashFunction(HashFunction descriptorHashFunction) {
			this.descriptorHashFunction = descriptorHashFunction;
		}
		
		public Builder setGSSLookupImpl(LookupImpl gssLookupImpl) {
			this.gssLookupImpl = gssLookupImpl;
			return this;
		}
		
		public Builder setSPPFLookupImpl(LookupImpl sppfLookupImpl) {
			this.sppfLookupImpl = sppfLookupImpl;
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
		return new StringBuilder()
			.append("GSS Type: ").append(gssType).append("\n")
			.append("Hash Function: ").append(sppfHashFunction).append("\n")
			.append("GSSLookup Impl: ").append(sppfLookupImpl).append("\n")
			.append("GSSLookup Strategy: ").append(gssLookupStrategy).append("\n")
			.append("SPPFLookup Impl: ").append(sppfLookupImpl).append("\n")
			.append("SPPFLookup Strategy: ").append(sppfLookupStrategy)
			.toString();
	}
	
}
