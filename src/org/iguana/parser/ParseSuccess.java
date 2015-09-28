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

package org.iguana.parser;

import iguana.parsetrees.sppf.NonterminalNode;
import iguana.parsetrees.sppf.SPPFVisitor;
import iguana.parsetrees.tree.*;
import iguana.utils.input.Input;
import org.iguana.util.ParseStatistics;

public class ParseSuccess extends AbstractParseResult {

	private final NonterminalNode sppfNode;
	private final ParseStatistics parseStatistics;
	private final Object tree;

	public ParseSuccess(NonterminalNode sppfNode, ParseStatistics parseStatistics, Input input) {
		super(input);
		this.sppfNode = sppfNode;
		this.parseStatistics = parseStatistics;
        tree = TermBuilder.build(sppfNode, TreeBuilderFactory.getDefault(input));
    }
	
	@Override
	public boolean isParseError() {
		return false;
	}

	@Override
	public boolean isParseSuccess() {
		return true;
	}

	@Override
	public ParseError asParseError() {
		throw new RuntimeException("Cannot call getParseError on Success.");
	}

	@Override
	public ParseSuccess asParseSuccess() {
		return this;
	}
	
	public NonterminalNode getRoot() {
		return sppfNode;
	}
	
	public ParseStatistics getStatistics() {
		return parseStatistics;
	}

    public Object getTree() {
        return tree;
    }

    @Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(parseStatistics.hashCode(), sppfNode.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof ParseSuccess))
			return false;
		
		ParseSuccess other = (ParseSuccess) obj;
		// TODO: add deep equals for sppf nodes
		return parseStatistics.equals(other.parseStatistics); // && sppfNode.deepEquals(other.sppfNode);
	}
	
	@Override
	public String toString() {
		return sppfNode + "\n" + parseStatistics;
	}
}
