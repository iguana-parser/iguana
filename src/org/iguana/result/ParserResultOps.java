package org.iguana.result;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.GrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.sppf.*;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.util.ParserLogger;

import java.util.List;

public class ParserResultOps implements ResultOps<NonPackedNode> {

    private ParserLogger logger = ParserLogger.getInstance();

    private static final NonPackedNode dummyNode = new NonPackedNode(-1) {
        @Override
        public PackedNode getChildAt(int index) {  throw new UnsupportedOperationException(); }

        @Override
        public List<PackedNode> getChildren() { throw new UnsupportedOperationException(); }

        @Override
        public int childrenCount() { throw new UnsupportedOperationException(); }

        @Override
        public GrammarSlot getGrammarSlot() { throw new UnsupportedOperationException(); }

        @Override
        public int getLeftExtent() { throw new UnsupportedOperationException(); }

        @Override
        public int getRightExtent() { throw new UnsupportedOperationException(); }

        @Override
        public <R> R accept(SPPFVisitor<R> visitAction) { throw new UnsupportedOperationException(); }
    };

    @Override
    public NonPackedNode dummy() {
        return dummyNode;
    }

    @Override
    public NonPackedNode base(TerminalGrammarSlot slot, int start, int end) {
        TerminalNode node = new TerminalNode(slot, start, end);
        logger.terminalNodeAdded();
        logger.log("Terminal node added %s", node);
        return node;
    }

    @Override
    public NonPackedNode merge(NonPackedNode current, NonPackedNode result1, NonPackedNode result2, BodyGrammarSlot<NonPackedNode> slot) {
        if (result1 == dummyNode)
            return result2;

        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result1);
        packedNode.setRightChild(result2);

        int rightExtent = (result2 != null) ? result2.getRightExtent() : result1.getRightExtent();

        logger.packedNodeAdded();
        logger.log("Packed node added %s", packedNode);

        if (current == null) {
            current = new IntermediateNode(rightExtent);
            ((IntermediateNode) current).addPackedNode(packedNode);
            logger.intermediateNodeAdded();
            logger.log("Intermediate node added %s", current);
        } else {
            ((IntermediateNode) current).addPackedNode(packedNode);
            if (current.getChildren().size() == 2) {
                logger.ambiguousNodeAdded();
                logger.log("Ambiguous node added: %s", current);
            }
        }

        return current;
    }

    @Override
    public NonPackedNode convert(NonPackedNode current, NonPackedNode result, EndGrammarSlot<NonPackedNode> slot, Object value) {
        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result);

        logger.packedNodeAdded();
        logger.log("Packed node added %s", packedNode);

        if (current == null) {
            if (value == null)
                current = new NonterminalNode(slot.getNonterminal(), result.getRightExtent());
            else
                current = new NonterminalNodeWithValue(slot.getNonterminal(), result.getRightExtent(), value);

            ((NonterminalOrIntermediateNode) current).addPackedNode(packedNode);
            logger.nonterminalNodeAdded();
            logger.log("Nonterminal node added %s", current);
        } else {
            ((NonterminalOrIntermediateNode) current).addPackedNode(packedNode);
            if (current.getChildren().size() == 2) {
                logger.ambiguousNodeAdded();
                logger.log("Ambiguous node added: %s", current);
            }
        }

        return current;
    }

    @Override
    public int getRightIndex(NonPackedNode result) {
        return result.getRightExtent();
    }

    @Override
    public Object getValue(NonPackedNode result) {
        if (result instanceof NonterminalNodeWithValue)
            return result.getValue();
        return null;
    }

    @Override
    public boolean isDummy(NonPackedNode result) {
        return result == dummyNode;
    }

}
