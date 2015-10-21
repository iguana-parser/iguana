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

import iguana.parsetrees.sppf.NonPackedNode;
import iguana.parsetrees.sppf.TerminalNode;
import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.gss.GSSNode;


public class TerminalTransition extends AbstractTransition {

    protected final TerminalGrammarSlot slot;
	
	private final Conditions preConditions;
	
	private final Conditions postConditions;

    public TerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest,
                              Conditions preConditions, Conditions postConditions, ParserRuntime runtime) {
		super(origin, dest, runtime);
        this.slot = slot;
        this.preConditions = preConditions;
        this.postConditions = postConditions;
    }

	@Override
	public void execute(Input input, GSSNode u, NonPackedNode node) {
		int i = node.getRightExtent();

		if (dest.getLabel() != null)
			execute(input, u, node, runtime.getEmptyEnvironment());
		
		if (preConditions.execute(input, u, i))
			return;
			
		TerminalNode cr = slot.getTerminalNode(input, i);
		
		if (cr == null) {
			runtime.recordParseError(input, i, origin, u);
			return;			
		}

		int rightExtent = cr.getRightExtent();
			
		if (postConditions.execute(input, u, rightExtent))
			return;
			
		NonPackedNode n = dest.isFirst() ? cr : dest.createIntermediateNode(node, cr);
				
		dest.execute(input, u, n);
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
	public void execute(Input input, GSSNode u, NonPackedNode node, Environment env) {

        int i = node.getRightExtent();

		runtime.setEnvironment(env);
		
		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, dest.getLabel()), i);

		if (preConditions.execute(input, u, i, runtime.getEvaluatorContext()))
			return;
		
		TerminalNode cr = slot.getTerminalNode(input, i);
		
		if (cr == null) {
			runtime.recordParseError(input, i, origin, u);
			return;
		}

		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(dest.getLabel(), cr);

		if (postConditions.execute(input, u, cr.getRightExtent(), runtime.getEvaluatorContext()))
			return;
		
		NonPackedNode n = dest.isFirst() ? cr : dest.createIntermediateNode(node, cr);
				
		dest.execute(input, u, n, env);
	}
	
}
