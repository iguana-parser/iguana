/*
 * Copyright (c) 2015, CWI
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
