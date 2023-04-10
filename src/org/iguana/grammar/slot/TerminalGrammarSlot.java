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

import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.IguanaRuntime;
import org.iguana.regex.matcher.Matcher;
import org.iguana.regex.matcher.MatcherFactory;
import org.iguana.result.Result;
import org.iguana.utils.collections.primitive.IntHashMap;
import org.iguana.utils.collections.primitive.OpenAddressingIntHashMap;
import org.iguana.utils.input.Input;

public class TerminalGrammarSlot implements GrammarSlot {

    // Record failures, it's cheaper for some complex regular expressions to do a lookup than to match again
    private static final Object failure = "failure";

    private final Terminal terminal;
    private final Matcher matcher;
    private IntHashMap<Object> terminalNodes;

    public TerminalGrammarSlot(Terminal terminal, MatcherFactory factory) {
        this.terminal = terminal;
        this.matcher = factory.getMatcher(terminal.getRegularExpression());
    }

    public <T extends Result> T getResult(Input input, int i, IguanaRuntime<T> runtime) {
        if (terminalNodes == null) {
            terminalNodes = new OpenAddressingIntHashMap<>();
        }
        Object node = terminalNodes.get(i);
        if (node == failure) return null;

        @SuppressWarnings("unchecked")
        T terminalNode = (T) node;
        if (terminalNode == null) {
            int length = matcher.match(input, i);
            if (length < 0) {
                terminalNodes.put(i, failure);
            } else {
                terminalNode = runtime.getResultOps().base(this, i, i + length);
                terminalNodes.put(i, terminalNode);
            }
        }
        return terminalNode;
    }


    public void recordFailure(int index) {
        if (terminalNodes == null) {
            terminalNodes = new OpenAddressingIntHashMap<>();
        }
        terminalNodes.put(index, failure);
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
