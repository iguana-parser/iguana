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
        public int getIndex() { throw new UnsupportedOperationException(); }

        @Override
        public boolean isDummy() {
            return true;
        }

        @Override
        public <R> R accept(SPPFVisitor<R> visitAction) { throw new UnsupportedOperationException(); }
    };

    private ParserLogger logger = ParserLogger.getInstance();

    @Override
    public NonPackedNode dummy() {
        return dummyNode;
    }

    @Override
    public NonPackedNode base(TerminalGrammarSlot slot, int start, int end) {
        TerminalNode node = new TerminalNode(slot, start, end);
        logger.terminalNodeAdded(node);
        return node;
    }

    @Override
    public NonPackedNode merge(NonPackedNode current, NonPackedNode result1, NonPackedNode result2, BodyGrammarSlot slot) {
        if (result1 == dummyNode)
            return result2;

        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result1);
        packedNode.setRightChild(result2);

        int rightExtent = (result2 != null) ? result2.getIndex() : result1.getIndex();

        logger.packedNodeAdded(packedNode);

        if (current == null) {
            current = new IntermediateNode(rightExtent);
            IntermediateNode intermediateNode = (IntermediateNode) current;
            intermediateNode.addPackedNode(packedNode);
            logger.intermediateNodeAdded(intermediateNode);
        } else {
            ((IntermediateNode) current).addPackedNode(packedNode);
            if (current.getChildren().size() == 2) {
                logger.ambiguousNodeAdded(current);
            }
        }

        return current;
    }

    @Override
    public NonPackedNode convert(NonPackedNode current, NonPackedNode result, EndGrammarSlot slot, Object value) {
        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result);

        logger.packedNodeAdded(packedNode);

        if (current == null) {
            if (value == null)
                current = new NonterminalNode(slot.getNonterminal(), result.getIndex());
            else
                current = new NonterminalNodeWithValue(slot.getNonterminal(), result.getIndex(), value);

            NonterminalNode nonterminalNode = (NonterminalNode) current;
            nonterminalNode.addPackedNode(packedNode);
            logger.nonterminalNodeAdded(nonterminalNode);
        } else {
            ((NonterminalOrIntermediateNode) current).addPackedNode(packedNode);
            if (current.getChildren().size() == 2) {
                logger.ambiguousNodeAdded(current);
            }
        }

        return current;
    }

}
