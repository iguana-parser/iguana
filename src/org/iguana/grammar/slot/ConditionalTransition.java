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
import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.exception.UnexpectedRuntimeTypeException;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;

public class ConditionalTransition extends AbstractTransition {
	
	private final Expression condition;
	
	private final BodyGrammarSlot ifFalse;

	public ConditionalTransition(Expression condition, BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this(condition, origin, dest, null);
	}
	
	public ConditionalTransition(Expression condition, BodyGrammarSlot origin, BodyGrammarSlot dest, BodyGrammarSlot ifFalse) {
		super(origin, dest);
		this.condition = condition;
		this.ifFalse = ifFalse;
	}

	public BodyGrammarSlot ifFalseDestination() {
		return ifFalse;
	}
	
	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node) {
		
		Object value = parser.evaluate(condition, parser.getEmptyEnvironment());
		
		if (!(value instanceof Boolean)) {
			throw new UnexpectedRuntimeTypeException(condition);
		}
		
		boolean isTrue = ((Boolean) value) == true;
		
		if (isTrue)
			dest.execute(parser, u, i, node);
		else if (ifFalse != null)
			ifFalse.execute(parser, u, i, node);
		// TODO: logging
	}

	@Override
	public String getLabel() {
		return String.format("[%s]", condition.toString());
	}

	@Override
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env) {
		
		Object value = parser.evaluate(condition, env);
		
		if (!(value instanceof Boolean)) {
			throw new UnexpectedRuntimeTypeException(condition);
		}
		
		boolean isTrue = ((Boolean) value) == true;
		
		if (isTrue)
			dest.execute(parser, u, i, node, env);
		else if (ifFalse != null)
			ifFalse.execute(parser, u, i, node, env);
		// TODO: logging
	}

	@Override
	public String getConstructorCode() {
		return null;
	}

}
