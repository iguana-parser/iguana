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

package org.iguana.parser.descriptor;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.gss.GSSNode;

public class Descriptor<T> {
	
	// L
	private BodyGrammarSlot<T> slot;
	
	// (L1, i)
	private GSSNode<T> gssNode;
	
	// (L, i, j), The label of SPPFNode is the same as the slot
	private T result;

	private Environment env;

	public Descriptor(BodyGrammarSlot<T> slot, GSSNode<T> gssNode, T result, Environment env) {
		init(slot, gssNode, result, env);
	}

	public void init(BodyGrammarSlot<T> slot, GSSNode<T> gssNode, T result, Environment env) {
		this.slot = slot;
		this.gssNode = gssNode;
		this.result = result;
		this.env = env;
	}
	
	public BodyGrammarSlot<T> getGrammarSlot() {
		return slot;
	}

	public GSSNode<T> getGSSNode() {
		return gssNode;
	}
	
	public T getResult() {
		return result;
	}

	public Environment getEnv() {
		return env;
	}

	@Override
	public String toString() {
		return String.format("(%s, %s, %s)", slot, gssNode, result);
	}
	
}
