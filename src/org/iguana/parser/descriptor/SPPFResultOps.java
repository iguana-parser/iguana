package org.iguana.parser.descriptor;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.ParserRuntime;
import org.iguana.sppf.*;
import org.iguana.util.ParserLogger;

public class SPPFResultOps implements ResultOps<NonPackedNode> {

    @Override
    public NonPackedNode dummy(int index) {
        return new DummyNode(index);
    }

    @Override
    public NonPackedNode base(TerminalGrammarSlot slot, int start, int end) {
        TerminalNode node = new TerminalNode(slot, start, end);
        ParserLogger.getInstance().terminalNodeAdded(node);
        return node;
    }

    @Override
    public NonPackedNode merge(NonPackedNode current, NonPackedNode result1, NonPackedNode result2, BodyGrammarSlot slot) {
        if (result1 instanceof DummyNode)
            return result2;

        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result1);
        packedNode.setRightChild(result2);

        ParserLogger.getInstance().packedNodeAdded(packedNode);

        if (current == null) {
            current = new IntermediateNode();
            ParserLogger.getInstance().intermediateNodeAdded((IntermediateNode) current);
        } else {
            if (current.getChildren().size() == 1)
                ParserLogger.getInstance().ambiguousNodeAdded((NonterminalOrIntermediateNode<?>) current);
        }

        ((IntermediateNode) current).addPackedNode(packedNode);
        return current;
    }

    @Override
    public NonPackedNode convert(NonPackedNode current, NonPackedNode result, EndGrammarSlot slot, Object value) {
        PackedNode packedNode = new PackedNode(slot);
        packedNode.setLeftChild(result);

        ParserLogger.getInstance().packedNodeAdded(packedNode);

        if (current == null) {
            if (value == null)
                current = new NonterminalNode(slot.getNonterminal());
            else
                current = new NonterminalNodeWithValue(slot.getNonterminal(), value);

            ParserLogger.getInstance().nonterminalNodeAdded((NonterminalNode) current);
        } else {
            if (current.getChildren().size() == 1)
                ParserLogger.getInstance().ambiguousNodeAdded((NonterminalOrIntermediateNode<?>) current);
        }

        ((NonterminalOrIntermediateNode) current).addPackedNode(packedNode);
        return current;
    }

    @Override
    public int getLeftIndex(NonPackedNode result) {
        return result.getLeftExtent();
    }

    @Override
    public int getRightIndex(NonPackedNode result) {
        return result.getRightExtent();
    }

    @Override
    public Object getValue(NonPackedNode result) {
        return ((NonterminalNodeWithValue) result).getValue();
    }
}
