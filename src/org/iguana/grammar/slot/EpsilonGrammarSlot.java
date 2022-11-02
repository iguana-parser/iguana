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

import org.iguana.utils.input.Input;
import org.iguana.datadependent.env.Environment;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.runtime.Position;
import org.iguana.grammar.slot.lookahead.FollowTest;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;

public class EpsilonGrammarSlot extends EndGrammarSlot {

	private final TerminalGrammarSlot epsilonSlot;

	public EpsilonGrammarSlot(
		Position position,
		NonterminalGrammarSlot nonterminal,
		TerminalGrammarSlot epsilonSlot,
		Conditions conditions
	) {
		super(position, nonterminal, null, null, null, conditions, FollowTest.DEFAULT);
		this.epsilonSlot = epsilonSlot;
	}

	@Override
	public <T extends Result> void execute(
		Input input,
		GSSNode<T> u,
		T result,
		Environment env,
		IguanaRuntime<T> runtime
	) {
		execute(input, u, result, (Object) null, runtime);
	}
	
	@Override
	public <T extends Result> void execute(
		Input input,
		GSSNode<T> u,
		T result,
		Object value,
		IguanaRuntime<T> runtime
	) {
        int i = result.isDummy() ? u.getInputIndex() : result.getRightExtent();

		int nextChar = input.charAt(i);
		FollowTest followTest = nonterminal.getFollowTest();
		if (followTest.test(nextChar)) {
			u.pop(input, this, epsilonSlot.getResult(input, i, this, u, runtime), value, runtime);
		} else {
			runtime.recordParseError(i, input, this, u, "Expected " + followTest + " but was " + (char) nextChar);
		}
	}

}
