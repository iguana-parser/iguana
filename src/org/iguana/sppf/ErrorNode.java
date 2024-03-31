package org.iguana.sppf;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.utils.collections.CollectionsUtil;

import java.util.Collections;
import java.util.List;

public class ErrorNode extends NonPackedNode {

    private final BodyGrammarSlot slot;
    private final int leftExtent;
    private final int rightExtent;
    private final List<NonPackedNode> children;

    public ErrorNode(BodyGrammarSlot slot, int leftExtent, int rightExtent) {
        this.slot = slot;
        this.leftExtent = leftExtent;
        this.rightExtent = rightExtent;
        this.children = Collections.emptyList();
    }

    public ErrorNode(BodyGrammarSlot slot, List<NonPackedNode> children) {
        if (children == null || children.isEmpty()) throw new RuntimeException("Children cannot be null or empty.");
        this.slot = slot;
        this.children = children;
        this.leftExtent = CollectionsUtil.first(children).getLeftExtent();
        this.rightExtent = CollectionsUtil.last(children).getRightExtent();
    }

    public List<NonPackedNode> getChildren() {
        return children;
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
