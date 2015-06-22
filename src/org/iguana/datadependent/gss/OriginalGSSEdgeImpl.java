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

package org.iguana.datadependent.gss;

import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.DummySlot;
import org.iguana.parser.GLLParser;
import org.iguana.parser.descriptor.Descriptor;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.DummyNode;
import org.iguana.sppf.NonPackedNode;

public class OriginalGSSEdgeImpl extends org.iguana.parser.gss.OriginalGSSEdgeImpl {
	
	private final Environment env;

	public OriginalGSSEdgeImpl(NonPackedNode node, GSSNode destination, Environment env) {
		super(node, destination);
		
		assert env != null;
		this.env = env;
	}
	
	@Override
	public Descriptor addDescriptor(GLLParser parser, GSSNode source, int inputIndex, NonPackedNode sppfNode) {
		
		GSSNode destination = getDestination();
		BodyGrammarSlot returnSlot = (BodyGrammarSlot) source.getGrammarSlot();
		
		Environment env = this.env;
		
		if (returnSlot.requiresBinding())
			env = returnSlot.doBinding(sppfNode, env);
		
		parser.setEnvironment(env);
		
		if (returnSlot.getConditions().execute(parser.getInput(), source, inputIndex, parser.getEvaluatorContext()))
			return null;
		
		env = parser.getEnvironment();
		
		NonPackedNode y;
		
		if (returnSlot.isLast() && !returnSlot.isEnd()) {
			parser.setCurrentEndGrammarSlot(DummySlot.getInstance());
			returnSlot.execute(parser, destination, inputIndex, DummyNode.getInstance(sppfNode.getLeftExtent(), inputIndex), env);
			
			if (parser.getCurrentEndGrammarSlot().isEnd()) {
				if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) {
					org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
					// TODO: support for return values
					y = parser.getNode(returnSlot, getNode(), sppfNode, env, dest.getData(), null); // use the original slot to create a node
				} else {
					// TODO: support for return values
					y = parser.getNode(returnSlot, getNode(), sppfNode, env, null, null); // use the original slot to create a node
				}
				returnSlot = parser.getCurrentEndGrammarSlot();
				env = parser.getEnvironment();
			} else
				return null;
		} else {
			if (destination instanceof org.iguana.datadependent.gss.GSSNode<?>) {
				org.iguana.datadependent.gss.GSSNode<?> dest = (org.iguana.datadependent.gss.GSSNode<?>) destination;
				// TODO: support for return values
				y = parser.getNode(returnSlot, getNode(), sppfNode, env, dest.getData(), null);
			} else {
				// TODO: support for return values
				y = parser.getNode(returnSlot, getNode(), sppfNode, env, null, null);
			}
		}
		
		if (!parser.hasDescriptor(returnSlot, destination, inputIndex, y, env))
			return new org.iguana.datadependent.descriptor.Descriptor(returnSlot, destination, inputIndex, y, env);
		
		return null;
	}

}
