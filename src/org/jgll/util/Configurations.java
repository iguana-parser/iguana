package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupImpl.*;
import static org.jgll.util.Configuration.LookupStrategy.*;

import java.util.ArrayList;
import java.util.List;


public class Configurations {
	
	public static final List<Configuration> configurations = new ArrayList<>();
	public static final List<Configuration> newConfigs = new ArrayList<>();
	public static final List<Configuration> originalConfigs = new ArrayList<>();
	
	private static final Configuration config1 = Configuration.DEFAULT;
	
	private static final Configuration config2 = Configuration.builder()
												.setGSSType(NEW)
											    .setGSSLookupStrategy(DISTRIBUTED)
											    .setGSSLookupImpl(HASH_MAP)
											    .setSPPFLookupStrategy(DISTRIBUTED)
											    .setSPPFLookupImpl(HASH_MAP)
											    .build();

	private static final Configuration config3 = Configuration.builder()
												.setGSSType(ORIGINAL)
												.setGSSLookupStrategy(DISTRIBUTED)
												.setGSSLookupImpl(ARRAY)
												.setSPPFLookupStrategy(DISTRIBUTED)
												.setSPPFLookupImpl(HASH_MAP)
												.build();	

	private static final Configuration config4 = Configuration.builder()
												.setGSSType(ORIGINAL)
											    .setGSSLookupStrategy(DISTRIBUTED)
											    .setGSSLookupImpl(HASH_MAP)
											    .setSPPFLookupStrategy(DISTRIBUTED)
											    .setSPPFLookupImpl(HASH_MAP)
											    .build();
	
	static {
		configurations.add(config1);
		configurations.add(config2);
		configurations.add(config3);
		configurations.add(config4);
		
		newConfigs.add(config1);
		newConfigs.add(config2);
		
		originalConfigs.add(config3);
		originalConfigs.add(config4);
	}
}
