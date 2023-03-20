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

package org.iguana.sppf;

import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.traversal.SPPFVisitor;

public abstract class TerminalNode extends NonPackedNode {

    private final int leftExtent;

    public TerminalNode(int leftExtent) {
        this.leftExtent = leftExtent;
    }

    @Override
    public <R> R accept(SPPFVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public int childrenCount() {
        return 0;
    }

    @Override
    public abstract TerminalGrammarSlot getGrammarSlot();

    @Override
    public PackedNode getChildAt(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLeftExtent() {
        return leftExtent;
    }

    @Override
    public void setAmbiguous(boolean ambiguous) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAmbiguous() {
        return false;
    }

    @Override
    public PackedNode getFirstPackedNode() {
        throw new UnsupportedOperationException();
    }
}
