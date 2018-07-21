package org.iguana.traversal;

import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.sppf.*;

import java.util.*;

import static java.util.Collections.emptyList;

public class DefaultSPPFToParseTreeVisitor<T> {

    private final ParseTreeBuilder<T> parseTreeBuilder;
    private final Set<Symbol> ignoreList;

    public DefaultSPPFToParseTreeVisitor(ParseTreeBuilder<T> parseTreeBuilder, Set<Symbol> ignoreList) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.ignoreList = ignoreList;
    }

    public T convert(NonterminalNode node) {
        if (node.isAmbiguous()) {
            throw new RuntimeException("Ambiguity found: " + node);
        }

        if (ignoreList.contains(node.getGrammarSlot().getNonterminal())) {
            return null;
        }

        PackedNode packedNode = node.getChildAt(0);
        switch (node.getGrammarSlot().getNodeType()) {
            case Layout:
            case Basic: {
                Deque<T> children;

                NonPackedNode child = packedNode.getLeftChild();
                if (child instanceof IntermediateNode) {
                    children = new ArrayDeque<>();
                    convert((IntermediateNode) child, children);
                } else {
                    children = new ArrayDeque<>(1);
                    T result = convert(child);
                    if (result != null) {
                        children.addFirst(result);
                    }
                }

                return parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getRule(), new ArrayList<>(children), packedNode.getLeftExtent(), packedNode.getRightExtent());
            }

            case Star: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                T result = convert(node.getChildAt(0).getLeftChild());
                List<T> children;
                if (result == null) {
                    children = emptyList();
                } else {
                    children = parseTreeBuilder.getChildren(convert(node.getChildAt(0).getLeftChild()));
                }
                return parseTreeBuilder.metaSymbolNode(symbol, new ArrayList<>(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Plus: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                Deque<T> children = new ArrayDeque<>();

                PackedNode pNode = node.getChildAt(0);

                while (true) {
                    if (pNode.getLeftChild() instanceof IntermediateNode) {
                        children.addFirst(convert(pNode.getLeftChild().getChildAt(0).getRightChild()));
                        if (pNode.getLeftChild().getChildAt(0).getLeftChild() instanceof IntermediateNode) { // + with separator
                            children.addFirst(convert(pNode.getLeftChild().getChildAt(0).getLeftChild().getChildAt(0).getRightChild()));
                            pNode = pNode.getLeftChild().getChildAt(0).getLeftChild().getChildAt(0).getLeftChild().getChildAt(0);
                        } else {
                            pNode = pNode.getLeftChild().getChildAt(0).getLeftChild().getChildAt(0);
                        }
                    } else {
                        children.addFirst(convert(pNode.getLeftChild()));
                        break;
                    }
                }

                return parseTreeBuilder.metaSymbolNode(symbol, new ArrayList<>(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Seq: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                NonPackedNode child = node.getChildAt(0).getLeftChild();
                Deque<T> children;
                if (child instanceof IntermediateNode) {
                    children = new ArrayDeque<>();
                    convert((IntermediateNode) child, children);
                } else {
                    children = new ArrayDeque<>();
                    children.addFirst(convert(child));
                }
                return parseTreeBuilder.metaSymbolNode(symbol, new ArrayList<>(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Alt:
            case Start:
            case Opt: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                List<T> children;
                T result = convert(packedNode.getLeftChild());
                if (result == null) {
                    children = emptyList();
                } else {
                    children = new ArrayList<>();
                    children.add(result);
                }
                return parseTreeBuilder.metaSymbolNode(symbol, children, node.getLeftExtent(), node.getRightExtent());
            }

            default:
                throw new RuntimeException("Unknown node type");
        }
    }

    private void convert(IntermediateNode node, Deque<T> children) {
        if (node.isAmbiguous()) {
            throw new RuntimeException("Ambiguity found: " + node);
        }

        NonPackedNode leftChild = node.getChildAt(0).getLeftChild();
        NonPackedNode rightChild = node.getChildAt(0).getRightChild();

        T result = convert(rightChild);
        if (result != null) {
            children.addFirst(result);
        }

        if (leftChild instanceof IntermediateNode) {
            convert((IntermediateNode) leftChild, children);
        }
        else {
            result = convert(leftChild);
            if (result != null) {
                children.addFirst(result);
            }
        }
    }

    private T convert(TerminalNode node) {
        if (ignoreList.contains(node.getGrammarSlot().getTerminal())) {
            return null;
        }
        return parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(), node.getIndex());
    }

    private T convert(NonPackedNode node) {
        if (node instanceof TerminalNode) {
            return convert((TerminalNode) node);
        } else if (node instanceof NonterminalNode) {
            return convert((NonterminalNode) node);
        }
        throw new RuntimeException("Can only be a terminal or nonterminal node");
    }

}
