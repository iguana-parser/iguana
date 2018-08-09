package org.iguana.traversal;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Sequence;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.sppf.*;
import org.iguana.traversal.exception.AmbiguityException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static java.util.Collections.emptyList;

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

        PackedNode packedNode = node.getChildAt(0);
        switch (node.getGrammarSlot().getNodeType()) {
            case Layout:
            case Basic: {
                Deque<T> children = new ArrayDeque<>();

                NonPackedNode child = packedNode.getLeftChild();
                if (child instanceof IntermediateNode) {
                    convertIntermediateNode((IntermediateNode) child, children);
                } else {
                    T result = convertPackedNode(child);
                    if (result != null) {
                        children.addFirst(result);
                    }
                }

                return parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getRule(), toList(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Star: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                T result = convertPackedNode(node.getChildAt(0).getLeftChild());
                List<T> children;
                if (result == null) {
                    children = emptyList();
                } else {
                    children = parseTreeBuilder.getChildren(result);
                }
                return parseTreeBuilder.metaSymbolNode(symbol, children, node.getLeftExtent(), node.getRightExtent());
            }


            case Plus: {
                Plus symbol = (Plus) packedNode.getGrammarSlot().getRule().getDefinition();

                PackedNode pNode = node.getChildAt(0);

                Deque<T> children = new ArrayDeque<>();

                while (true) {
                    if (pNode.getLeftChild() instanceof IntermediateNode) {
                        NonterminalNode nextPlusNode = convertUnderPlus(symbol, (IntermediateNode) pNode.getLeftChild(), children);
                        if (nextPlusNode == null) {
                            break;
                        } else {
                            pNode = nextPlusNode.getChildAt(0);
                        }
                    } else {
                        T result = convertPackedNode(pNode.getLeftChild());
                        if (result != null) {
                            children.addFirst(result);
                        }
                        break;
                    }
                }

                return parseTreeBuilder.metaSymbolNode(symbol, toList(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Seq: {
                Sequence symbol = (Sequence) packedNode.getGrammarSlot().getRule().getDefinition();
                NonPackedNode child = node.getChildAt(0).getLeftChild();
                Deque<T> children = new ArrayDeque<>();
                if (child instanceof IntermediateNode) {
                    convertIntermediateNode((IntermediateNode) child, children);
                } else {
                    children.addFirst(convertPackedNode(child));
                }
                return parseTreeBuilder.metaSymbolNode(symbol, toList(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Start: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                Deque<T> children = new ArrayDeque<>();
                if (packedNode.getLeftChild() instanceof IntermediateNode) {
                    convertIntermediateNode((IntermediateNode) packedNode.getLeftChild(), children);
                } else {
                    T result = convertPackedNode(packedNode.getLeftChild());
                    if (result != null) {
                        children.add(result);
                    }
                }
                return parseTreeBuilder.metaSymbolNode(symbol, toList(children), node.getLeftExtent(), node.getRightExtent());
            }


            case Alt:
            case Opt: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                List<T> children;
                T result = convertPackedNode(packedNode.getLeftChild());
                if (result == null) {
                    children = emptyList();
                } else {
                    children = new ArrayList<>(1);
                    children.add(result);
                }
                return parseTreeBuilder.metaSymbolNode(symbol, children, node.getLeftExtent(), node.getRightExtent());
            }

            default:
                throw new RuntimeException("Unknown node type");
        }
    }

    private void convertIntermediateNode(IntermediateNode node, Deque<T> children) {
        if (node.isAmbiguous()) {
            throw new AmbiguityException(node, input);
        }

        NonPackedNode leftChild = node.getChildAt(0).getLeftChild();
        NonPackedNode rightChild = node.getChildAt(0).getRightChild();

        T result = convertPackedNode(rightChild);
        if (result != null) {
            children.addFirst(result);
        }

        if (leftChild instanceof IntermediateNode) {
            convertIntermediateNode((IntermediateNode) leftChild, children);
        }
        else {
            result = convertPackedNode(leftChild);
            if (result != null) {
                children.addFirst(result);
            }
        }
    }

    private NonterminalNode convertUnderPlus(Plus plus, IntermediateNode node, Deque<T> children) {
        if (node.isAmbiguous()) {
            throw new RuntimeException("Ambiguity found: " + node);
        }

        NonPackedNode leftChild = node.getChildAt(0).getLeftChild();
        NonPackedNode rightChild = node.getChildAt(0).getRightChild();

        T result = convertPackedNode(rightChild);
        if (result != null) {
            children.addFirst(result);
        }

        if (leftChild instanceof IntermediateNode) {
            return convertUnderPlus(plus, (IntermediateNode) leftChild, children);
        }
        else {
            if (leftChild instanceof NonterminalNode && plus.getName().equals(leftChild.getChildAt(0).getGrammarSlot().getRule().getDefinition().getName())) {
                return (NonterminalNode) leftChild;
            }
            result = convertPackedNode(leftChild);
            if (result != null) {
                children.addFirst(result);
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

    private T convertPackedNode(NonPackedNode node) {
        if (node instanceof TerminalNode) {
            return convertTerminalNode((TerminalNode) node);
        } else if (node instanceof NonterminalNode) {
            return convertNonterminalNode((NonterminalNode) node);
        }
        throw new RuntimeException("Can only be a terminal or nonterminal node");
    }

    private List<T> toList(Deque<T> deque) {
        return deque.size() == 0 ? emptyList() : new ArrayList<>(deque);
    }

}
