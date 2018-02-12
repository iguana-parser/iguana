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

import iguana.utils.collections.hash.MurmurHash3;
import iguana.utils.input.Input;
import org.iguana.sppf.NonterminalNode;
import org.iguana.util.JsonSerializer;
import org.iguana.util.ParseStatistics;
import org.iguana.util.SPPFJsonSerializer;

public class ParseSuccess extends AbstractParseResult {

	private final NonterminalNode sppfNode;
	private final ParseStatistics parseStatistics;

	public ParseSuccess(NonterminalNode sppfNode, ParseStatistics parseStatistics, Input input) {
		super(input);
		this.sppfNode = sppfNode;
		this.parseStatistics = parseStatistics;
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
	
	public NonterminalNode getSPPFNode() {
		return sppfNode;
	}
	
	public ParseStatistics getStatistics() {
		return parseStatistics;
	}

    @Override
	public int hashCode() {
		return MurmurHash3.fn().apply(parseStatistics, sppfNode);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof ParseSuccess))
			return false;
		
		ParseSuccess other = (ParseSuccess) obj;
		return parseStatistics.equals(other.parseStatistics) &&
			   SPPFJsonSerializer.serialize(sppfNode).equals(SPPFJsonSerializer.serialize(other.sppfNode));
	}
	
	@Override
	public String toString() {
		return parseStatistics + "\n";// + SPPFToJavaCode.get(sppfNode) + "\n";
	}
}
