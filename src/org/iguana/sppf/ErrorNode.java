package org.iguana.sppf;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.traversal.SPPFVisitor;

public class ErrorNode extends NonPackedNode {

    private final BodyGrammarSlot slot;
    private final int leftExtent;
    private final int rightExtent;

    public ErrorNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
        this.slot = slot;
        this.leftExtent = leftExtent;
        this.rightExtent = rightExtent;
    }

    @Override
    public int getRightExtent() {
        return rightExtent;
    }

    @Override
    public SPPFNode getChildAt(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int childrenCount() {
        return 0;
    }

    @Override
    public GrammarSlot getGrammarSlot() {
        return slot;
    }

    @Override
    public int getLeftExtent() {
        return leftExtent;
    }

    @Override
    public <R> R accept(SPPFVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setAmbiguous(boolean ambiguous) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAmbiguous() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PackedNode getFirstPackedNode() {
        throw new UnsupportedOperationException();
    }
}
