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

import iguana.regex.matcher.Matcher;
import iguana.regex.matcher.MatcherFactory;
import iguana.utils.collections.IntHashMap;
import iguana.utils.collections.OpenAddressingIntHashMap;
import iguana.utils.input.Input;
import org.iguana.grammar.condition.Conditions;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.gss.GSSNode;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.Result;

import java.util.ArrayList;
import java.util.List;

public class TerminalGrammarSlot implements GrammarSlot {
	
	private final Terminal terminal;
    private final Matcher matcher;
	private IntHashMap<Object> terminalNodes;
    private final Conditions preConditions;
    private final Conditions postConditions;

    // Record failures, it's cheaper for some complex regular expressions to do a lookup than to match again
	private static final Object failure = "failure";

	public TerminalGrammarSlot(Terminal terminal, MatcherFactory factory, Conditions preConditions, Conditions postConditions) {
		this.terminal = terminal;
        this.preConditions = preConditions;
        this.postConditions = postConditions;
        this.matcher = factory.getMatcher(terminal.getRegularExpression());
    }

	public <T extends Result> List<T> getResult(Input input, int i, BodyGrammarSlot slot, GSSNode<T> gssNode, IguanaRuntime<T> runtime) {
	    if (terminalNodes == null) {
	        terminalNodes = new OpenAddressingIntHashMap<>();
        }
		Object nodes = terminalNodes.get(i);
	    if (nodes == failure) {
	        return null;
        }

        if (preConditions.execute(input, slot, gssNode, i, runtime)) {
            terminalNodes.put(i, failure);
            return null;
        }

		if (nodes == null) {
			List<Integer> endIndexes = matcher.match(input, i);

			if (endIndexes.isEmpty()) {
				nodes = null;
				terminalNodes.put(i, failure);
			} else {
				final List<T> curNodes = new ArrayList<>();
				for (Integer endIndex: endIndexes) {
					if (postConditions.execute(input, slot, gssNode, i, endIndex, runtime)) {
						terminalNodes.put(i, failure);
						return null;
					}
					curNodes.add(runtime.getResultOps().base(this, i, endIndex));
				}
				nodes = curNodes;
				terminalNodes.put(i, nodes);
			}
		}
		return (List<T>) nodes;
	}

	public int countTerminalNodes() {
		return terminalNodes.size();
	}

    public Terminal getTerminal() {
        return terminal;
    }

	@Override
	public String toString() {
		return terminal.toString();
	}

    @Override
	public void reset() {
		terminalNodes = null;
	}

}
