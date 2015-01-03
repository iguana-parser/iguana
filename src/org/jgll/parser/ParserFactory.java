package org.jgll.parser;

import org.jgll.parser.gss.lookup.DistributedGSSLookupImpl;
import org.jgll.parser.gss.lookup.GSSLookup;
import org.jgll.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.DistributedDescriptorLookupImpl;
import org.jgll.sppf.lookup.DistributedSPPFLookupImpl;
import org.jgll.sppf.lookup.GlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.OriginalDistributedSPPFLookupImpl;
import org.jgll.sppf.lookup.OriginalGlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupStrategy;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class ParserFactory {
	
	public static GLLParser getParser() {
		return getParser(Configuration.builder().build());
	}

	public static GLLParser getParser(Configuration config) {
		if (config.getGSSType() == GSSType.NEW) {
			return newParser(config);
		} else {
			return originalParser(config);
		}
	}
	
	private static GLLParser newParser(Configuration config) {
		return new NewGLLParserImpl(getGSSLookup(config), getSPPFLookup(config), getDescriptorLookup(config));		
	}
	
	private static GLLParser originalParser(Configuration config) {
		return new OriginalGLLParserImpl(getGSSLookup(config), getSPPFLookup(config), getDescriptorLookup(config));
	}

	private static GSSLookup getGSSLookup(Configuration config) {
		if (config.getGSSLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			return new DistributedGSSLookupImpl();
		} else {
			return new GlobalHashGSSLookupImpl();
		}
	}
	
	private static SPPFLookup getSPPFLookup(Configuration config) {
		if (config.getGSSType() == GSSType.NEW) {
			if (config.getGSSLookupStrategy() == LookupStrategy.DISTRIBUTED) {
				return new DistributedSPPFLookupImpl(config.getHashFunction());
			} else {
				return new GlobalSPPFLookupImpl(config.getHashFunction());
			}			
		} else {
			if (config.getGSSLookupStrategy() == LookupStrategy.DISTRIBUTED) {
				return new OriginalDistributedSPPFLookupImpl(config.getHashFunction());
			} else {
				return new OriginalGlobalSPPFLookupImpl(config.getHashFunction());
			}
		}
	}
	
	private static DescriptorLookup getDescriptorLookup(Configuration config) {
		return new DistributedDescriptorLookupImpl();
	}

}
