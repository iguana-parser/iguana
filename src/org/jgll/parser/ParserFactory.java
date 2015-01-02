package org.jgll.parser;

import org.jgll.parser.gss.lookup.DistributedGSSLookupImpl;
import org.jgll.parser.gss.lookup.GSSLookup;
import org.jgll.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.DistributedDescriptorLookupImpl;
import org.jgll.sppf.lookup.DistributedSPPFLookupImpl;
import org.jgll.sppf.lookup.GlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupStrategy;

public class ParserFactory {
	
	public static GLLParser getParser() {
		return newParser();
	}
	
	public static GLLParser newParser(Configuration config) {
		return new NewGLLParserImpl(getGSSLookup(config), getSPPFLookup(config), getDescriptorLookup(config));		
	}
	
	public static GLLParser newParser() {
		return newParser(Configuration.builder().build());
	}
	
	public static GLLParser originalParser(Configuration config) {
		return new OriginalGLLParserImpl(getGSSLookup(config), getSPPFLookup(config), getDescriptorLookup(config));
	}

	public static GLLParser originalParser() {
		return originalParser(Configuration.builder().setGSSType(GSSType.ORIGINAL).build());
	}
	
	private static GSSLookup getGSSLookup(Configuration config) {
		if (config.getLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			return new DistributedGSSLookupImpl();
		} else {
			return new GlobalHashGSSLookupImpl();
		}
	}
	
	public static SPPFLookup getSPPFLookup(Configuration config) {
		if (config.getLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			return new DistributedSPPFLookupImpl(config.getHashFunction());
		} else {
			return new GlobalSPPFLookupImpl(config.getHashFunction());
		}
	}
	
	public static DescriptorLookup getDescriptorLookup(Configuration config) {
		return new DistributedDescriptorLookupImpl();
	}

}
