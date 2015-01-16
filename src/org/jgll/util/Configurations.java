package org.jgll.util;

import static org.jgll.util.Configuration.GSSType.*;

import java.util.ArrayList;
import java.util.List;

import org.jgll.util.Configuration.LookupImpl;
import org.jgll.util.Configuration.LookupStrategy;


public class Configurations {
	
	public static final List<Configuration> all_configs = new ArrayList<>();
	public static final List<Configuration> newConfigs = new ArrayList<>();
	public static final List<Configuration> originalConfigs = new ArrayList<>();
	
	static {
		
		for (LookupStrategy descriLookupStrategy : LookupStrategy.values()) {
			for (LookupStrategy gssLookupStrategy : LookupStrategy.values()) {
				for (LookupImpl gssLookupImpl : LookupImpl.values()) {
					for (LookupStrategy sppfLookupStrategy : LookupStrategy.values()) {
						for (LookupImpl sppfLookupImpl : LookupImpl.values()) {
							newConfigs.add(Configuration.builder().setGSSType(NEW)
																  .setDescriptorLookupStrategy(descriLookupStrategy)
																  .setGSSLookupStrategy(gssLookupStrategy)
																  .setGSSLookupImpl(gssLookupImpl)
																  .setSPPFLookupStrategy(sppfLookupStrategy)
																  .setSPPFLookupImpl(sppfLookupImpl).build());
						}
					}
				}
			}			
		}
		
		for (LookupStrategy descriLookupStrategy : LookupStrategy.values()) {
			for (LookupStrategy gssLookupStrategy : LookupStrategy.values()) {
				for (LookupImpl gssLookupImpl : LookupImpl.values()) {
					for (LookupStrategy sppfLookupStrategy : LookupStrategy.values()) {
						for (LookupImpl sppfLookupImpl : LookupImpl.values()) {
							originalConfigs.add(Configuration.builder().setGSSType(ORIGINAL)
																	   .setDescriptorLookupStrategy(descriLookupStrategy)
												   					   .setGSSLookupStrategy(gssLookupStrategy)
												   					   .setGSSLookupImpl(gssLookupImpl)
												   					   .setSPPFLookupStrategy(sppfLookupStrategy)
												   					   .setSPPFLookupImpl(sppfLookupImpl).build());
						}
					}
				}
			}			
		}
		
		all_configs.addAll(newConfigs);
		all_configs.addAll(originalConfigs);
	}
}
