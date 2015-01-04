package org.jgll.util;

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
	
	private LookupStrategy gssLookupStrategy;
	
	private LookupStrategy sppfLookupStrategy;
	
	private Configuration(Builder builder) {
		this.gssType = builder.gssType;
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
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private GSSType gssType;
		private LookupImpl gssLookupImpl;
		private LookupImpl sppfLookupImpl;
		private LookupStrategy gssLookupStrategy;
		private LookupStrategy sppfLookupStrategy;
		
		public Builder() {
			this.gssType = GSSType.NEW;
			this.gssLookupImpl = LookupImpl.ARRAY;
			this.sppfLookupImpl = LookupImpl.HASH_MAP;
			this.gssLookupStrategy = LookupStrategy.DISTRIBUTED;
			this.sppfLookupStrategy = LookupStrategy.DISTRIBUTED;
		}
		
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
			.append("GSSLookup Impl: ").append(sppfLookupImpl).append("\n")
			.append("GSSLookup Strategy: ").append(gssLookupStrategy).append("\n")
			.append("SPPFLookup Impl: ").append(sppfLookupImpl).append("\n")
			.append("SPPFLookup Strategy: ").append(sppfLookupStrategy)
			.toString();
	}
	
}
