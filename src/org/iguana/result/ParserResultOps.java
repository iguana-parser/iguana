package org.iguana.result;

import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.TerminalGrammarSlot;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.*;
import org.iguana.util.ParserLogger;

public class ParserResultOps implements ResultOps<NonPackedNode> {

    private ParserLogger logger = ParserLogger.getInstance();

    private static final DummyNode dummyNode = new DummyNode();

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

        logger.packedNodeAdded();
        logger.log("Packed node added %s", packedNode);

        if (current == null) {
            current = new IntermediateNode();
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
                current = new NonterminalNode(slot.getNonterminal());
            else
                current = new NonterminalNodeWithValue(slot.getNonterminal(), value);

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
    public int getRightIndex(NonPackedNode result, GSSNode<NonPackedNode> gssNode) {
        if (result == dummyNode) {
            return gssNode.getInputIndex();
        }
        return result.getRightExtent();
    }

    @Override
    public Object getValue(NonPackedNode result) {
        if (result instanceof NonterminalNodeWithValue)
            return ((NonterminalNodeWithValue) result).getValue();
        return null;
    }

}
