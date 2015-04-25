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

package org.iguana.util;

import static org.iguana.util.Configuration.GSSType.*;

import java.util.ArrayList;
import java.util.List;

import org.iguana.util.Configuration.LookupImpl;
import org.iguana.util.Configuration.LookupStrategy;


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
