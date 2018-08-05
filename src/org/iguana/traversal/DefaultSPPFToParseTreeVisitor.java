package org.iguana.traversal;

import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Sequence;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.sppf.*;

import java.util.*;

import static java.util.Collections.emptyList;

public class DefaultSPPFToParseTreeVisitor<T> {

    private final ParseTreeBuilder<T> parseTreeBuilder;
    private final boolean ignoreLayout;

    public DefaultSPPFToParseTreeVisitor(ParseTreeBuilder<T> parseTreeBuilder, Set<String> ignoreList) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.ignoreLayout = !ignoreList.isEmpty();
    }

    public T convert(NonterminalNode node) {
        if (node.isAmbiguous()) {
            throw new RuntimeException("Ambiguity found: " + node);
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
                    convert((IntermediateNode) child, children);
                } else {
                    T result = convert(child);
                    if (result != null) {
                        children.addFirst(result);
                    }
                }

                return parseTreeBuilder.nonterminalNode(packedNode.getGrammarSlot().getRule(), toList(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Star: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                T result = convert(node.getChildAt(0).getLeftChild());
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
                        T result = convert(pNode.getLeftChild());
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
                    convert((IntermediateNode) child, children);
                } else {
                    children.addFirst(convert(child));
                }
                return parseTreeBuilder.metaSymbolNode(symbol, toList(children), node.getLeftExtent(), node.getRightExtent());
            }

            case Start: {
                Symbol symbol = packedNode.getGrammarSlot().getRule().getDefinition();
                Deque<T> children = new ArrayDeque<>();
                if (packedNode.getLeftChild() instanceof IntermediateNode) {
                    convert((IntermediateNode) packedNode.getLeftChild(), children);
                } else {
                    T result = convert(packedNode.getLeftChild());
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
                T result = convert(packedNode.getLeftChild());
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

    private NonterminalNode convertUnderPlus(Plus plus, IntermediateNode node, Deque<T> children) {
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
            return convertUnderPlus(plus, (IntermediateNode) leftChild, children);
        }
        else {
            if (leftChild instanceof NonterminalNode && plus.getName().equals(leftChild.getChildAt(0).getGrammarSlot().getRule().getDefinition().getName())) {
                return (NonterminalNode) leftChild;
            }
            result = convert(leftChild);
            if (result != null) {
                children.addFirst(result);
            }
        }
        return null;
    }

    private T convert(TerminalNode node) {
        if (ignoreLayout && node.getGrammarSlot().getTerminal().getNodeType() == TerminalNodeType.Layout) {
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

    private List<T> toList(Deque<T> deque) {
        return deque.size() == 0 ? emptyList() : new ArrayList<>(deque);
    }

}
