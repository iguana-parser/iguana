package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;
import static org.jgll.util.Configuration.LookupImpl.*;
import static org.jgll.util.Configuration.LookupStrategy.*;

import java.util.ArrayList;
import java.util.List;

import org.jgll.util.Configuration.GSSType;


public class Configurations {
	
	public static final List<Configuration> all_configs = new ArrayList<>();
	public static final List<Configuration> newConfigs = new ArrayList<>();
	public static final List<Configuration> originalConfigs = new ArrayList<>();
	
	private static final Configuration new_config1 = Configuration.DEFAULT;
	
	private static final Configuration new_config2 = Configuration.builder()
												.setGSSType(NEW)
											    .setGSSLookupStrategy(DISTRIBUTED)
											    .setGSSLookupImpl(HASH_MAP)
											    .setSPPFLookupStrategy(DISTRIBUTED)
											    .setSPPFLookupImpl(HASH_MAP)
											    .build();

	private static final Configuration original_config1 = Configuration.builder()
												.setGSSType(ORIGINAL)
												.setGSSLookupStrategy(DISTRIBUTED)
												.setGSSLookupImpl(ARRAY)
												.setSPPFLookupStrategy(DISTRIBUTED)
												.setSPPFLookupImpl(HASH_MAP)
												.build();	

	private static final Configuration original_config2 = Configuration.builder()
												.setGSSType(ORIGINAL)
											    .setGSSLookupStrategy(DISTRIBUTED)
											    .setGSSLookupImpl(HASH_MAP)
											    .setSPPFLookupStrategy(DISTRIBUTED)
											    .setSPPFLookupImpl(HASH_MAP)
											    .build();
	
	static {
		
		for (GSSType gssType : GSSType.values()) {
			
		}
		
		
		all_configs.add(new_config1);
		all_configs.add(new_config2);
		all_configs.add(original_config1);
		all_configs.add(original_config2);
		
		newConfigs.add(new_config1);
		newConfigs.add(new_config2);
		
		originalConfigs.add(original_config1);
		originalConfigs.add(original_config2);
	}
}
