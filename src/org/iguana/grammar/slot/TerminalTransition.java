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
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.parser.gss.GSSNode;

public class TerminalTransition<T> extends AbstractTransition<T> {

    protected final TerminalGrammarSlot<T> slot;
	
	private final Conditions preConditions;
	
	private final Conditions postConditions;

	public TerminalTransition(TerminalGrammarSlot<T> slot, BodyGrammarSlot<T> origin, BodyGrammarSlot<T> dest,
							  Conditions preConditions, Conditions postConditions, ParserRuntime runtime, ResultOps<T> ops) {
		super(origin, dest, runtime, ops);
        this.slot = slot;
        this.preConditions = preConditions;
        this.postConditions = postConditions;
	}

	@Override
	public void execute(Input input, GSSNode<T> u, T result) {
		int i = ops.getRightIndex(result, u);

		if (dest.getLabel() != null) {
            execute(input, u, result, runtime.getEmptyEnvironment());
            return;
        }
		
		if (preConditions.execute(input, u, i))
			return;
			
		T cr = slot.getResult(input, i);
		
		if (cr == null) {
			runtime.recordParseError(input, i, origin, u);
			return;			
		}

		int rightExtent = ops.getRightIndex(cr);
			
		if (postConditions.execute(input, u, rightExtent))
			return;
			
		T n = dest.isFirst() ? cr : ops.merge(null, result, cr, dest);
				
		dest.execute(input, u, n);
	}
	
	public TerminalGrammarSlot getSlot() {
		return slot;
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
	public void execute(Input input, GSSNode<T> u, T node, Environment env) {

        int i = ops.getRightIndex(node, u);

		runtime.setEnvironment(env);
		
		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(String.format(Expression.LeftExtent.format, dest.getLabel()), i);

		if (preConditions.execute(input, u, i, runtime.getEvaluatorContext()))
			return;
		
		T cr = slot.getResult(input, i);
		
		if (cr == null) {
			runtime.recordParseError(input, i, origin, u);
			return;
		}

		if (dest.getLabel() != null)
			runtime.getEvaluatorContext().declareVariable(dest.getLabel(), cr);

		if (postConditions.execute(input, u, ops.getRightIndex(cr), runtime.getEvaluatorContext()))
			return;
		
		T n = dest.isFirst() ? cr : ops.merge(null, node, cr, dest);
				
		dest.execute(input, u, n, runtime.getEnvironment());
	}
	
}
