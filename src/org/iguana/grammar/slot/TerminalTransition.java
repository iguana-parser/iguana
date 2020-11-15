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

import iguana.utils.input.Input;
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;

import java.util.List;

public class TerminalTransition extends AbstractTransition {

    protected final TerminalGrammarSlot slot;
	
	private final Conditions preConditions;
	
	private final Conditions postConditions;

	public TerminalTransition(TerminalGrammarSlot slot, BodyGrammarSlot origin, BodyGrammarSlot dest, Conditions preConditions, Conditions postConditions) {
		super(origin, dest);
        this.slot = slot;
        this.preConditions = preConditions;
        this.postConditions = postConditions;
	}

	public TerminalGrammarSlot getSlot() {
		return slot;
	}
	
	@Override
	public String getLabel() {
		return (dest.getLabel() != null? dest.getLabel() + ":" : "") + getSlot();
	}
	
	@Override
	public <T extends Result> void execute(Input input, GSSNode<T> u, T result, Environment env, IguanaRuntime<T> runtime) {
        int i = result.isDummy() ? u.getInputIndex() : result.getIndex();

		runtime.setEnvironment(env);
		
		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, dest.getLabel()), i);

		if (preConditions.execute(input, origin, u, i, runtime.getEvaluatorContext(), runtime))
			return;

		List<T> crs = slot.getResult(input, i, origin, u, runtime);
		
		if (crs == null) {
			runtime.recordParseError(i, origin, u);
			return;
		}

		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(dest.getLabel(), crs);

		for (T cr: crs) {
			if (!postConditions.execute(input, origin, u, cr.getLeftExtent(), cr.getIndex(), runtime.getEvaluatorContext(), runtime)) {
				T n = dest.isFirst() ? cr : runtime.getResultOps().merge(null, result, cr, dest);

//				dest.execute(input, u, n, runtime.getEnvironment(), runtime);
				runtime.scheduleDescriptor(dest, u, n, runtime.getEnvironment());

			}
		}
	}
	
}
