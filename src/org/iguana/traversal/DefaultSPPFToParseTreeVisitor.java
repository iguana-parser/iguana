package org.iguana.traversal;

import iguana.utils.input.Input;
import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.sppf.*;
import org.iguana.traversal.exception.AmbiguityException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 *
 * Unambiguous Nonterminal nodes have only one child:
 *
 * Unambiguous Intermediate nodes two child:
 *  - The left child is an intermediate node and the right child a nonterminal or terminal node
 *  - Both left and right children are nonterminal or terminal nodes
 *
 */
public class DefaultSPPFToParseTreeVisitor<T> {

    private final ParseTreeBuilder<T> parseTreeBuilder;
    private final Input input;
    private final boolean ignoreLayout;

    public DefaultSPPFToParseTreeVisitor(ParseTreeBuilder<T> parseTreeBuilder, Input input, boolean ignoreLayout) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.input = input;
        this.ignoreLayout = ignoreLayout;
    }

    public T convertNonterminalNode(NonterminalNode node) {
        if (node.isAmbiguous()) {
            throw new AmbiguityException(node, input);
        }

        if (ignoreLayout && node.getGrammarSlot().getNonterminal().getNodeType() == NonterminalNodeType.Layout) {
            return null;
        }

        NonPackedNode firstChild = (NonPackedNode) node.getChildAt(0);

        int leftExtent = node.getLeftExtent();
        int rightExtent = node.getRightExtent();

        switch (node.getGrammarSlot().getNodeType()) {
            case Layout:
            case Basic:
                return convertBasicAndLayout(node.getEndGrammarSlot(), firstChild, leftExtent, rightExtent);

            case Star:
                return convertStar(firstChild, (Star) node.getRule().getDefinition(), leftExtent, rightExtent);

            case Plus:
                return convertPlus(firstChild, (Plus) node.getRule().getDefinition(), leftExtent, rightExtent);

            case Seq:
                return convertSeq(firstChild, (Group) node.getRule().getDefinition(), leftExtent, rightExtent);

            case Start:
                return convertStart(firstChild, (Start) node.getRule().getDefinition(), leftExtent, rightExtent);

            case Alt:
            case Opt:
                return convertAltOpt(firstChild, node.getRule().getDefinition(), leftExtent, rightExtent);

            default:
                throw new RuntimeException("Unknown node type");
        }
    }

    private T convertBasicAndLayout(BodyGrammarSlot slot, NonPackedNode child, int leftExtent, int rightExtent) {
        int bodySize = slot.getRule().getBody().size();
        if (ignoreLayout) { // Layout is insert between symbols in the body of a rule
            bodySize /= 2 + 1;
        }
        List<T> children = new ArrayList<>(bodySize);

        if (child instanceof IntermediateNode) {
            convertIntermediateNode((IntermediateNode) child, children);
        } else {
            T result = convertSPPFNode(child);
            if (result != null) {
                children.add(result);
            }
        }

        reverse(children);
        return parseTreeBuilder.nonterminalNode(slot.getRule(), children, leftExtent, rightExtent);
    }

    private T convertStar(NonPackedNode node, Star symbol, int leftExtent, int rightExtent) {
        T result = convertSPPFNode(node);
        List<T> children;
        if (result == null) {
            children = emptyList();
        } else {
            children = parseTreeBuilder.getChildren(result);
        }
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertPlus(NonPackedNode leftChild, Plus symbol, int leftExtent, int rightExtent) {
        NonPackedNode node = leftChild;

        List<T> children = new ArrayList<>();

        while (true) {
            if (node instanceof IntermediateNode) {
                NonterminalNode nextPlusNode = convertUnderPlus(symbol, (IntermediateNode) node, children);
                if (nextPlusNode == null) {
                    break;
                } else {
                    node = (NonPackedNode) nextPlusNode.getChildAt(0);
                }
            } else {
                T result = convertSPPFNode(node);
                if (result != null) {
                    children.add(result);
                }
                break;
            }
        }

        reverse(children);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertSeq(NonPackedNode node, Group symbol, int leftExtent, int rightExtent) {
        List<T> children = new ArrayList<>();
        if (node instanceof IntermediateNode) {
            convertIntermediateNode((IntermediateNode) node, children);
        } else {
            children.add(convertSPPFNode(node));
        }

        reverse(children);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertStart(NonPackedNode node, Start symbol, int leftExtent, int rightExtent) {
        List<T> children = new ArrayList<>(ignoreLayout ? 3 : 1); // Layout is inserted before and after the start symbol
        if (node instanceof IntermediateNode) {
            convertIntermediateNode((IntermediateNode) node, children);
        } else {
            T result = convertSPPFNode(node);
            if (result != null) {
                children.add(result);
            }
        }

        reverse(children);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertAltOpt(NonPackedNode node, Symbol symbol, int leftExtent, int rightExtent) {
        List<T> children;
        T result = convertSPPFNode(node);
        if (result == null) {
            children = emptyList();
        } else {
            children = new ArrayList<>(1);
            children.add(result);
        }
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private void convertIntermediateNode(IntermediateNode node, List<T> children) {
        if (node.isAmbiguous()) {
            throw new AmbiguityException(node, input);
        }

        NonPackedNode leftChild = (NonPackedNode) node.getChildAt(0);
        NonPackedNode rightChild = (NonPackedNode) node.getChildAt(1);

        T result = convertSPPFNode(rightChild);
        if (result != null) {
            children.add(result);
        }

        if (leftChild instanceof IntermediateNode) {
            convertIntermediateNode((IntermediateNode) leftChild, children);
        }
        else {
            result = convertSPPFNode(leftChild);
            if (result != null) {
                children.add(result);
            }
        }
    }

    private NonterminalNode convertUnderPlus(Plus plus, IntermediateNode node, List<T> children) {
        if (node.isAmbiguous()) {
            throw new RuntimeException("Ambiguity found: " + node);
        }

        NonPackedNode leftChild = (NonPackedNode) node.getChildAt(0);
        NonPackedNode rightChild = (NonPackedNode) node.getChildAt(1);

        T result = convertSPPFNode(rightChild);
        if (result != null) {
            children.add(result);
        }

        if (leftChild instanceof IntermediateNode) {
            return convertUnderPlus(plus, (IntermediateNode) leftChild, children);
        }
        else {
            if (leftChild instanceof NonterminalNode) {
                RuntimeRule rule = ((NonterminalNode) leftChild).getRule();
                if (rule.getDefinition() != null && plus.getName().equals(rule.getDefinition().getName())) {
                    return (NonterminalNode) leftChild;
                }
            }
            result = convertSPPFNode(leftChild);
            if (result != null) {
                children.add(result);
            }
        }
        return null;
    }

    private T convertTerminalNode(TerminalNode node) {
        if (ignoreLayout && node.getGrammarSlot().getTerminal().getNodeType() == TerminalNodeType.Layout) {
            return null;
        }
        return parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getIndex());
    }

    private T convertSPPFNode(SPPFNode node) {
        if (node instanceof TerminalNode) {
            return convertTerminalNode((TerminalNode) node);
        } else if (node instanceof NonterminalNode) {
            return convertNonterminalNode((NonterminalNode) node);
        }
        throw new RuntimeException("Can only be a terminal or nonterminal node, but got: " + node);
    }

    private void reverse(List<T> list) {
        int size = list.size();
        if (size == 0 || size == 1) {
            return;
        }
        for (int i = 0; i < size / 2; i++) {
            T tmp = list.get(size - i - 1);
            list.set(size - i - 1, list.get(i));
            list.set(i, tmp);
        }
    }

}
