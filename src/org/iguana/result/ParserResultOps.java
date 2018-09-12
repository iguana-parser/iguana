package org.iguana.result;

import org.iguana.grammar.slot.*;
import org.iguana.sppf.*;
import org.iguana.traversal.SPPFVisitor;
import org.iguana.util.ParserLogger;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ParserResultOps implements ResultOps<NonPackedNode> {

    private static final NonPackedNode dummyNode = new NonPackedNode() {
        @Override
        public PackedNode getChildAt(int index) {  throw new UnsupportedOperationException(); }

        @Override
        public int childrenCount() { throw new UnsupportedOperationException(); }

        @Override
        public GrammarSlot getGrammarSlot() { throw new UnsupportedOperationException(); }

        @Override
        public int getLeftExtent() { throw new UnsupportedOperationException(); }

        @Override
        public int getIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDummy() {
            return true;
        }

        @Override
        public <R> R accept(SPPFVisitor<R> visitAction) { throw new UnsupportedOperationException(); }

        @Override
        public void setAmbiguous(boolean ambiguous) {
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public PackedNode getFirstPackedNode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "$";
        }
    };

    private ParserLogger logger = ParserLogger.getInstance();

    private Map<NonPackedNode, List<PackedNode>> packedNodesMap = new IdentityHashMap<>();

    @Override
    public NonPackedNode dummy() {
        return dummyNode;
    }

    @Override
    public TerminalNode base(TerminalGrammarSlot slot, int start, int end) {
        TerminalNode node;
        if (start == end) {
            node = new EmptyTerminalNode(slot, start);
        } else if (slot.getTerminal().getNodeType() == TerminalNodeType.Keyword) {
            node = new KeywordTerminalNode(slot, start);
        } else {
            node = new DefaultTerminalNode(slot, start, end);
        }
        logger.terminalNodeAdded(node);
        return node;
    }

    @Override
    public NonPackedNode merge(NonPackedNode current, NonPackedNode result1, NonPackedNode result2, BodyGrammarSlot slot) {
        if (result1 == dummyNode)
            return result2;

        if (current == null) {
            current = new IntermediateNode(slot, result1, result2);
            logger.intermediateNodeAdded((IntermediateNode) current);
        } else {
            List<PackedNode> packedNodes = packedNodesMap.computeIfAbsent(current, key -> new ArrayList<>());

            if (!current.isAmbiguous()) {
                PackedNode firstPackedNode = current.getFirstPackedNode();
                logger.packedNodeAdded(firstPackedNode);
                packedNodes.add(firstPackedNode);

                current.setAmbiguous(true);
                logger.ambiguousNodeAdded(current);
            }

            PackedNode packedNode = new PackedNode(slot, result1, result2);
            packedNodes.add(packedNode);
            logger.packedNodeAdded(packedNode);
        }

        return current;
    }

    @Override
    public NonPackedNode convert(NonPackedNode current, NonPackedNode result, EndGrammarSlot slot, Object value) {
        if (current == null) {
            if (value == null)
                current = new NonterminalNode(slot, result, result.getLeftExtent(), result.getRightExtent());
            else
                current = new NonterminalNodeWithValue(slot, result, result.getLeftExtent(), result.getRightExtent(), value);

            logger.nonterminalNodeAdded((NonterminalNode) current);
        } else {
            List<PackedNode> packedNodes = packedNodesMap.computeIfAbsent(current, key -> new ArrayList<>());

            if (!current.isAmbiguous()) {
                PackedNode firstPackedNode = current.getFirstPackedNode();
                logger.packedNodeAdded(firstPackedNode);
                packedNodes.add(firstPackedNode);

                current.setAmbiguous(true);
                logger.ambiguousNodeAdded(current);
            }

            PackedNode packedNode = new PackedNode(slot, result);
            packedNodes.add(packedNode);
            logger.packedNodeAdded(packedNode);
        }

        return current;
    }

    public List<PackedNode> getPackedNodes(NonPackedNode node) {
        return packedNodesMap.get(node);
    }

}
