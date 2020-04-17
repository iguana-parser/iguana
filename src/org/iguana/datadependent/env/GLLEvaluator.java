/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
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

package org.iguana.datadependent.env;

import org.iguana.datadependent.env.array.ArrayEvaluatorContext;
import org.iguana.datadependent.env.intarray.IntArrayEvaluatorContext;
import org.iguana.datadependent.env.persistent.PersistentEvaluatorContext;
import org.iguana.datadependent.env.simple.SimpleEvaluatorContext;
import org.iguana.util.Configuration;

public class GLLEvaluator {
	
	public static IEvaluatorContext getDefaultEvaluatorContext() {
		// return new PersistentEvaluatorContext(input);
		// return new SimpleEvaluatorContext(input);
		return new ArrayEvaluatorContext();
	}
	
	public static IEvaluatorContext getEvaluatorContext(Configuration config) {
		switch(config.getEnvImpl()) {
			case ARRAY: 
				return new ArrayEvaluatorContext();
			case INT_ARRAY:
				return new IntArrayEvaluatorContext();
			case HASH_MAP:
				return new SimpleEvaluatorContext();
			case TRIE: 
				return new PersistentEvaluatorContext();
			default:
				throw new RuntimeException("Should not have happened!");
		}
	}

}
