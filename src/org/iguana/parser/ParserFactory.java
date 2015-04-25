package org.iguana.parser;

import org.iguana.grammar.Grammar;
import org.iguana.parser.gss.lookup.DistributedGSSLookupImpl;
import org.iguana.parser.gss.lookup.GSSLookup;
import org.iguana.parser.gss.lookup.GlobalHashGSSLookupImpl;
import org.iguana.parser.lookup.DescriptorLookup;
import org.iguana.parser.lookup.DistributedDescriptorLookupImpl;
import org.iguana.parser.lookup.GlobalDescriptorLookupImpl;
import org.iguana.sppf.lookup.DistributedSPPFLookupImpl;
import org.iguana.sppf.lookup.GlobalSPPFLookupImpl;
import org.iguana.sppf.lookup.OriginalDistributedSPPFLookupImpl;
import org.iguana.sppf.lookup.OriginalGlobalSPPFLookupImpl;
import org.iguana.sppf.lookup.SPPFLookup;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.iguana.util.Configuration.GSSType;
import org.iguana.util.Configuration.LookupStrategy;

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
				return new GlobalSPPFLookupImpl(input, grammar);				
			} else {
				return new OriginalGlobalSPPFLookupImpl(input, grammar);
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
