package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.parser.gss.lookup.DistributedGSSLookupImpl;
import org.jgll.parser.gss.lookup.GSSLookup;
import org.jgll.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.jgll.parser.lookup.DescriptorLookup;
import org.jgll.parser.lookup.DistributedDescriptorLookupImpl;
import org.jgll.parser.lookup.GlobalDescriptorLookupImpl;
import org.jgll.sppf.lookup.DistributedSPPFLookupImpl;
import org.jgll.sppf.lookup.GlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.OriginalDistributedSPPFLookupImpl;
import org.jgll.sppf.lookup.OriginalGlobalSPPFLookupImpl;
import org.jgll.sppf.lookup.SPPFLookup;
import org.jgll.util.Configuration;
import org.jgll.util.Configuration.GSSType;
import org.jgll.util.Configuration.LookupStrategy;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class ParserFactory {
	
	public static GLLParser getParser(Configuration config, Input input, Grammar grammar) {
		if (config.getGSSType() == GSSType.NEW) {
			return newParser(config, input, grammar);
		} else {
			return originalParser(config, input, grammar);
		}
	}
	
	private static GLLParser newParser(Configuration config, Input input, Grammar grammar) {
		return new NewGLLParserImpl(config,
									getGSSLookup(config, input, grammar), 
								    getSPPFLookup(config, input, grammar), 
								    getDescriptorLookup(config, input, grammar));		
	}
	
	private static GLLParser originalParser(Configuration config, Input input, Grammar grammar) {
		return new OriginalGLLParserImpl(config,
										 getGSSLookup(config, input, grammar), 
				 					     getSPPFLookup(config, input, grammar), 
				 					     getDescriptorLookup(config, input, grammar));
	}

	private static GSSLookup getGSSLookup(Configuration config, Input input, Grammar grammar) {
		if (config.getGSSLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			return new DistributedGSSLookupImpl();
		} else {
			return new GlobalHashGSSLookupImpl();
		}
	}
	
	private static SPPFLookup getSPPFLookup(Configuration config, Input input, Grammar grammar) {
		if (config.getSPPFLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			if (config.getGSSType() == GSSType.NEW) {
				return new DistributedSPPFLookupImpl(input);
			} else {
				return new OriginalDistributedSPPFLookupImpl(input);
			}
		} else {
			if (config.getGSSType() == GSSType.NEW) {
				return new GlobalSPPFLookupImpl(input);				
			} else {
				return new OriginalGlobalSPPFLookupImpl(input);
			}			
		}		
	}
	
	private static DescriptorLookup getDescriptorLookup(Configuration config, Input input, Grammar grammar) {
		if (config.getDescriptorLookupStrategy() == LookupStrategy.DISTRIBUTED) {
			return new DistributedDescriptorLookupImpl(input, grammar);			
		} else {
			return new GlobalDescriptorLookupImpl(input, grammar);
		}
	}

}
