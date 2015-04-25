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

package org.iguana.parser.gss.lookup;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.util.Input;

public class ArrayNodeLookup extends AbstractNodeLookup {

	private GSSNode[] gssNodes;
	private int length;
	
	public ArrayNodeLookup(Input input) {
		length = input.length();
	}
	
	@Override
	public GSSNode getOrElseCreate(GrammarSlot slot, int i) {
		GSSNode v;
		if ((v = gssNodes[i]) == null) {
			v = new GSSNode(slot, i);
			gssNodes[i] = v;
		}
		return v;
	}

	@Override
	public GSSNode get(int i) {
		return gssNodes[i];
	}

	@Override
	public void reset(Input input) {
		super.reset(input);
		length = input.length();
		gssNodes = null;		
	}
	
	@Override
	public Iterable<GSSNode> getNodes() {
		return Arrays.stream(gssNodes).filter(n -> n != null).collect(Collectors.toList());
	}

	@Override
	public ArrayNodeLookup init() {
		super.init();
		gssNodes = new GSSNode[length];
		return this;
	}

	@Override
	public boolean isInitialized() {
		return gssNodes != null;
	}

}
