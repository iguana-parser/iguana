package org.jgll.util;

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
	
	public static class Builder {
		
		private GSSType gssType;
		private HashFunction hashFunction;
		private LookupType lookupType;
		
		public Configuration build() {
			return new Configuration(this);
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
