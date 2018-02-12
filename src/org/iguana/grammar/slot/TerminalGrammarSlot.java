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
import iguana.utils.input.Input;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.ParserRuntime;
import org.iguana.sppf.TerminalNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TerminalGrammarSlot extends AbstractGrammarSlot {
	
	private final Terminal terminal;
    private final Matcher matcher;
    private final Map<Integer, TerminalNode> terminalNodes;
    private final String terminalName;

	public TerminalGrammarSlot(Terminal terminal, MatcherFactory factory, String terminalName, ParserRuntime runtime) {
		super(runtime, Collections.emptyList());
		this.terminal = terminal;
        this.matcher = factory.getMatcher(terminal.getRegularExpression());
        this.terminalNodes = new HashMap<>();
        this.terminalName = terminalName;
    }

    public TerminalGrammarSlot(Terminal terminal, MatcherFactory factory, ParserRuntime runtime) {
        this(terminal, factory, null, runtime);
    }

	public TerminalNode getTerminalNode(Input input, int i) {
		return terminalNodes.computeIfAbsent(i, k -> {
			int length = matcher.match(input, i);
			if (length < 0) {
				return null;
			} else {
				TerminalNode t = new TerminalNode(this, i, i + length);
				runtime.terminalNodeAdded(t);
				return t;
			}
		});
	}

    public Terminal getTerminal() {
        return terminal;
    }

    @Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

	@Override
	public String toString() {
		return terminal.toString();
	}

    @Override
	public void reset(Input input) {
		terminalNodes.clear();
	}

}
