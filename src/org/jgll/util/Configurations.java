package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupImpl.*;
import static org.jgll.util.Configuration.LookupStrategy.*;

import java.util.ArrayList;
import java.util.List;


public class Configurations {
	
	public static final List<Configuration> configurations = new ArrayList<>();
	
	static {
		
		configurations.add(Configuration.DEFAULT);
		
		configurations.add(Configuration.builder()
				.setGSSType(NEW)
			    .setGSSLookupStrategy(DISTRIBUTED)
			    .setGSSLookupImpl(HASH_MAP)
			    .setSPPFLookupStrategy(DISTRIBUTED)
			    .setSPPFLookupImpl(HASH_MAP)
			    .build());

//		configurations.add(Configuration.builder()
//				.setGSSType(ORIGINAL)
//				.setGSSLookupStrategy(DISTRIBUTED)
//				.setGSSLookupImpl(ARRAY)
//				.setSPPFLookupStrategy(DISTRIBUTED)
//				.setSPPFLookupImpl(HASH_MAP)
//				.build());
//
//		configurations.add(Configuration.builder()
//				.setGSSType(ORIGINAL)
//			    .setGSSLookupStrategy(DISTRIBUTED)
//			    .setGSSLookupImpl(HASH_MAP)
//			    .setSPPFLookupStrategy(DISTRIBUTED)
//			    .setSPPFLookupImpl(HASH_MAP)
//			    .build());
	
	}
		
}
