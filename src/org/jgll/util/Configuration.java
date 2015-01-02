package org.jgll.util;

import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class Configuration {
	
	private final GSSType gssType;
	
	private final HashFunction hashFunction;
	
	private final LookupImpl lookupImpl;
	
	private LookupStrategy lookupStrategy;
	
	private Configuration(Builder builder) {
		this.gssType = builder.gssType;
		this.hashFunction = builder.hashFunction;
		this.lookupImpl = builder.lookupImpl;
		this.lookupStrategy = builder.lookupStrategy;
	}
	
	public GSSType getGSSType() {
		return gssType;
	}
	
	public HashFunction getHashFunction() {
		return hashFunction;
	}
	
	public LookupImpl getLookupImpl() {
		return lookupImpl;
	}
	
	public LookupStrategy getLookupStrategy() {
		return lookupStrategy;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private GSSType gssType = GSSType.NEW;
		private HashFunction hashFunction = HashFunctions.primeMultiplication;
		private LookupImpl lookupImpl = LookupImpl.HASH_MAP;
		private LookupStrategy lookupStrategy = LookupStrategy.DISTRIBUTED;
		
		public Configuration build() {
			return new Configuration(this);
		}
		
		public Builder setGSSType(GSSType gssType) {
			this.gssType = gssType;
			return this;
		}
		
		public Builder setHashFunction(HashFunction hashFunction) {
			this.hashFunction = hashFunction;
			return this;
		}
		
		public Builder setLookupType(LookupImpl lookupType) {
			this.lookupImpl = lookupType;
			return this;
		}
		
		public Builder setLookupStrategy(LookupStrategy lookupStrategy) {
			this.lookupStrategy = lookupStrategy;
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
	
}
