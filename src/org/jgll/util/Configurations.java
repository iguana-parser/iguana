package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupImpl.*;
import static org.jgll.util.Configuration.LookupStrategy.*;

import org.jgll.grammar.Grammar;


public class Configurations {

	public static final Configuration DEFAULT(Grammar grammar, Input input) { 
		return Configuration.builder(grammar, input).setGSSType(NEW)
													.setGSSLookupStrategy(DISTRIBUTED)
													.setGSSLookupImpl(ARRAY)
													.setSPPFLookupStrategy(DISTRIBUTED)
													.setSPPFLookupImpl(HASH_MAP)
													.build();
	}
	
	public static final Configuration CONFIG_1(Grammar grammar, Input input) {
		return Configuration.builder(grammar, input).setGSSType(NEW)
												    .setGSSLookupStrategy(DISTRIBUTED)
												    .setGSSLookupImpl(HASH_MAP)
												    .setSPPFLookupStrategy(DISTRIBUTED)
												    .setSPPFLookupImpl(HASH_MAP)
												    .build();
	}
	

	public static final Configuration CONFIG_2(Grammar grammar, Input input) { 
		return Configuration.builder(grammar, input).setGSSType(ORIGINAL)
													.setGSSLookupStrategy(DISTRIBUTED)
													.setGSSLookupImpl(ARRAY)
													.setSPPFLookupStrategy(DISTRIBUTED)
													.setSPPFLookupImpl(HASH_MAP)
													.build();
	}

	
	public static final Configuration CONFIG_3(Grammar grammar, Input input) { 
		return Configuration.builder(grammar, input).setGSSType(ORIGINAL)
												    .setGSSLookupStrategy(DISTRIBUTED)
												    .setGSSLookupImpl(HASH_MAP)
												    .setSPPFLookupStrategy(DISTRIBUTED)
												    .setSPPFLookupImpl(HASH_MAP)
												    .build();
	}
	
}
