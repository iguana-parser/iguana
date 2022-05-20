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

import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.traversal.SPPFVisitor;

public class NonterminalNode extends NonPackedNode {

	private final EndGrammarSlot slot;

	private final NonPackedNode child;

	private final int leftExtent;

	private final int rightExtent;

    private boolean ambiguous;

	public NonterminalNode(EndGrammarSlot slot, NonPackedNode child, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.child = child;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
	}

    @Override
    public SPPFNode getChildAt(int index) {
	    if (index == 0) {
	        return child;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int childrenCount() {
        return 1;
    }

    @Override
	public NonterminalGrammarSlot getGrammarSlot() {
		return slot.getNonterminal();
	}

	public EndGrammarSlot getEndGrammarSlot() {
        return slot;
    }

    public RuntimeRule getRule() {
        return slot.getRule();
    }

	@Override
	public <R> R accept(SPPFVisitor<R> visitAction) {
		return visitAction.visit(this);
	}

    @Override
    public int getLeftExtent() {
        return leftExtent;
    }

    @Override
    public void setAmbiguous(boolean ambiguous) {
        this.ambiguous = ambiguous;
    }

    @Override
    public boolean isAmbiguous() {
        return ambiguous;
    }

    @Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot, getLeftExtent(), getRightExtent());
	}

    @Override
    public int getRightExtent() {
        return rightExtent;
    }

    @Override
    public PackedNode getFirstPackedNode() {
        return new PackedNode(slot, child);
    }
}
