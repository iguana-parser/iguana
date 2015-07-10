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

package org.iguana.grammar.slot;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Input;


public class TerminalTransition extends AbstractTransition {
	
	protected final TerminalGrammarSlot slot;
	
	private final Conditions preConditions;
	
	private final Conditions postConditions;
	
	public TerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, 
			                          Conditions preConditions, Conditions postConditions) {
		super(origin, dest);
		this.slot = slot;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		if (dest.getLabel() != null)
			execute(parser, u, i, node, parser.getEmptyEnvironment());
		
		Input input = parser.getInput();
			
		if (preConditions.execute(input, u, i))
			return;
			
		TerminalNode cr = slot.getTerminalNode(input, i);
		
		if (cr == null) {
			parser.recordParseError(origin);
			return;			
		}

		int rightExtent = cr.getRightExtent();
			
		if (postConditions.execute(input, u, rightExtent))
			return;
			
		NonPackedNode n = dest.isFirst() ? cr : new IntermediateNode(dest, node.getLeftExtent(), rightExtent);
		dest.execute(parser, u, rightExtent, n);
	}
	
	public TerminalGrammarSlot getSlot() {
		return slot;
	}
	
	@Override
	public String getConstructorCode() {
		return new StringBuilder()
			.append("new NonterminalTransition(")
			.append("slot" + slot.getId()).append(", ")
			.append("slot" + origin.getId()).append(", ")
			.append("slot" + dest.getId()).append(", ")
			.toString();
	}

	@Override
	public String getLabel() {
		return (dest.getLabel() != null? dest.getLabel() + ":" : "") + getSlot();
	}
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		Input input = parser.getInput();
		
		parser.setEnvironment(env);
		
		if (dest.getLabel() != null)
			parser.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, dest.getLabel()), i);

		if (preConditions.execute(input, u, i, parser.getEvaluatorContext()))
			return;
		
		TerminalNode cr = slot.getTerminalNode(input, i);
		
		if (cr == null) {
			parser.recordParseError(origin);
			return;
		}

		int rightExtent = cr.getRightExtent();
				
		if (dest.getLabel() != null)
			parser.getEvaluatorContext().declareVariable(dest.getLabel(), cr);

		if (postConditions.execute(input, u, rightExtent, parser.getEvaluatorContext()))
			return;
		
		NonPackedNode n = dest.isFirst() ? cr : new org.iguana.datadependent.sppf.IntermediateNode(dest, node.getLeftExtent(), rightExtent, env);
		dest.execute(parser, u, rightExtent, n, env);
	}
	
}
