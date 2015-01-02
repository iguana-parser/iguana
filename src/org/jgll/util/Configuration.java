package org.jgll.util;

import org.jgll.parser.HashFunctions;
import org.jgll.util.hashing.hashfunction.HashFunction;

public class Configuration {
	
	private final GSSType gssType;
	
	private final HashFunction hashFunction;
	
	private final LookupType lookupType;
	
	private Configuration(Builder builder) {
		this.gssType = builder.gssType;
		this.hashFunction = builder.hashFunction;
		this.lookupType = builder.lookupType;
	}
	
	public GSSType getGSSType() {
		return gssType;
	}
	
	public HashFunction getHashFunction() {
		return hashFunction;
	}
	
	public LookupType getLookupType() {
		return lookupType;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private GSSType gssType = GSSType.NEW;
		private HashFunction hashFunction = HashFunctions.primeMultiplication;
		private LookupType lookupType = LookupType.MAP_DISTRIBUTED;
		
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
		
		public Builder setLookupType(LookupType lookupType) {
			this.lookupType = lookupType;
			return this;
		}
	}
	
	public static enum GSSType {
		NEW,
		ORIGINAL
	}
	
	public static enum LookupType {
		ARRAY_DISTRIBUTED,
		ARRAY_GLOBAL,
		MAP_DISTRIBUTED,
		MAP_GLOBAL
	}
	
}
