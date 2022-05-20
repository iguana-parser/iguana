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

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.traversal.SPPFVisitor;

public class PackedNode implements SPPFNode {

    private final BodyGrammarSlot slot;

    private NonPackedNode leftChild;

    private NonPackedNode rightChild;

    public PackedNode(BodyGrammarSlot slot) {
        this.slot = slot;
    }

    public PackedNode(BodyGrammarSlot slot, NonPackedNode leftChild) {
        this(slot, leftChild, null);
    }

    public PackedNode(BodyGrammarSlot slot, NonPackedNode leftChild, NonPackedNode rightChild) {
        this.slot = slot;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public void setLeftChild(NonPackedNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(NonPackedNode rightChild) {
        this.rightChild = rightChild;
    }

    private int getPivot() {
        return leftChild.getRightExtent();
    }

    @Override
    public BodyGrammarSlot getGrammarSlot() {
        return slot;
    }

    @Override
    public int getLeftExtent() {
        return leftChild.getLeftExtent();
    }

    @Override
    public int getRightExtent() {
        return (rightChild != null) ? rightChild.getRightExtent() : leftChild.getRightExtent();
    }

    @Override
    public String toString() {
        return String.format("(%s, %d)", slot, getPivot());
    }

    @Override
    public <R> R accept(SPPFVisitor<R> visitAction) {
        return visitAction.visit(this);
    }

    public NonPackedNode getLeftChild() {
        return leftChild;
    }

    public NonPackedNode getRightChild() {
        return rightChild;
    }

    @Override
    public NonPackedNode getChildAt(int index) {
        if (index == 0)
            return leftChild;
        else if (index == 1)
            return rightChild;
        else
            throw new RuntimeException("index should be only 0 or 1.");
    }

    @Override
    public int childrenCount() {
        return (rightChild == null) ? 1 : 2;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PackedNode)) return false;

        PackedNode other = (PackedNode) obj;
        return slot == other.slot && getPivot() == other.getPivot();
    }
}
