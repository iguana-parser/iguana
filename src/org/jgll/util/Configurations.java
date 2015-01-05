package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupImpl.*;
import static org.jgll.util.Configuration.LookupStrategy.*;


public class Configurations {
	
	public static final Configuration CONFIG_1 = Configuration.builder()
													.setGSSType(NEW)
												    .setGSSLookupStrategy(DISTRIBUTED)
												    .setGSSLookupImpl(HASH_MAP)
												    .setSPPFLookupStrategy(DISTRIBUTED)
												    .setSPPFLookupImpl(HASH_MAP)
												    .build();
	

	public static final Configuration CONFIG_2 = Configuration.builder()
													.setGSSType(ORIGINAL)
													.setGSSLookupStrategy(DISTRIBUTED)
													.setGSSLookupImpl(ARRAY)
													.setSPPFLookupStrategy(DISTRIBUTED)
													.setSPPFLookupImpl(HASH_MAP)
													.build();

	public static final Configuration CONFIG_3 =  Configuration.builder()
													.setGSSType(ORIGINAL)
												    .setGSSLookupStrategy(DISTRIBUTED)
												    .setGSSLookupImpl(HASH_MAP)
												    .setSPPFLookupStrategy(DISTRIBUTED)
												    .setSPPFLookupImpl(HASH_MAP)
												    .build();
	
}
