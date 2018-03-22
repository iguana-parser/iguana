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
import org.iguana.parser.ParserRuntime;
import org.iguana.parser.descriptor.ResultOps;
import org.iguana.parser.gss.GSSNode;

public class ReturnTransition<T> extends AbstractTransition<T> {
	
	private final Expression expression;

	public ReturnTransition(Expression expression, BodyGrammarSlot<T> origin, BodyGrammarSlot<T> dest, ParserRuntime runtime, ResultOps<T> ops) {
		super(origin, dest, runtime, ops);
		this.expression = expression;
	}

	@Override
	public void execute(Input input, GSSNode<T> u, T result) {
	   Object value = runtime.evaluate(expression, runtime.getEmptyEnvironment());
	   ((EndGrammarSlot<T>) dest).execute(input, u, result, value);
	}

	@Override
	public String getLabel() {
		return expression.toString();
	}

	@Override
	public void execute(Input input, GSSNode<T> u, T result, Environment env) {
		Object value = runtime.evaluate(expression, env);
		((EndGrammarSlot<T>) dest).execute(input, u, result, value);
	}

}
