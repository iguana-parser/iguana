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

package org.iguana.parser.gss.lookup;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSEdge;
import org.iguana.parser.gss.GSSNode;
import org.iguana.parser.gss.GSSNodeData;
/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface GSSLookup {
	
	/**
	 * Returns an already existing GSS node with the given grammar slot and input 
	 * index, or creates a new GSS node with given parameters.  
	 */
	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex);
	
	/**
	 * Returns an already existing GSS node with the given parametrs. If no such
	 * GSS node exists, returns null.
	 */
	public GSSNode hasGSSNode(GrammarSlot slot, int inputIndex);
	
	public boolean getGSSEdge(GSSNode gssNode, GSSEdge edge);

	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public void reset();
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public <T> GSSNode getGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data);
	
	public <T> GSSNode hasGSSNode(GrammarSlot slot, int inputIndex, GSSNodeData<T> data);
	
}	
