package org.iguana.traversal;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.BodyGrammarSlot;
import org.iguana.grammar.slot.NonterminalNodeType;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.Group;
import org.iguana.grammar.symbol.Plus;
import org.iguana.grammar.symbol.Star;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.symbol.Symbol;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.EmptyTerminalNode;
import org.iguana.sppf.ErrorNode;
import org.iguana.sppf.IntermediateNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.PackedNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.traversal.exception.AmbiguityException;
import org.iguana.util.visualization.SPPFToDot;
import org.iguana.utils.input.Input;
import org.iguana.utils.visualization.DotGraph;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Unambiguous Nonterminal nodes have only one child.
 * Unambiguous Intermediate nodes two children:
 * - The left child is an intermediate node and the right child a nonterminal or terminal node
 * - Both left and right children are nonterminal or terminal nodes
 */
public class DefaultSPPFToParseTreeVisitor<T> implements SPPFVisitor<T> {

    private final ParseTreeBuilder<T> parseTreeBuilder;
    private final Input input;
    private final boolean ignoreLayout;
    private final ParserResultOps resultOps;

    public DefaultSPPFToParseTreeVisitor(
            ParseTreeBuilder<T> parseTreeBuilder,
            Input input,
            boolean ignoreLayout,
            ParserResultOps resultOps) {
        this.parseTreeBuilder = parseTreeBuilder;
        this.input = input;
        this.ignoreLayout = ignoreLayout;
        this.resultOps = resultOps;
    }

    @Override
    public T visit(TerminalNode node) {
        if (ignoreLayout && node.getGrammarSlot().getTerminal().getNodeType() == TerminalNodeType.Layout) {
            return null;
        }
        return parseTreeBuilder.terminalNode(node.getGrammarSlot().getTerminal(), node.getLeftExtent(),
                node.getRightExtent());
    }

    @Override
    public T visit(NonterminalNode node) {
        if (node.isAmbiguous()) {
            handleAmbiguousNode(node);
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

    @Override
    public Object visit(IntermediateNode node) {
        if (node.isAmbiguous()) {
            throw new AmbiguityException(node, input);
        }

        List<T> children = new ArrayList<>();

        NonPackedNode leftChild = (NonPackedNode) node.getChildAt(0);
        NonPackedNode rightChild = (NonPackedNode) node.getChildAt(1);

        T result = rightChild.accept(this);
        if (result != null) {
            children.add(result);
        }

        addChildren(leftChild.accept(this), children);

        return children;
    }

    @Override
    public Object visit(PackedNode node) {
        throw new RuntimeException("Should not visit packed nodes.");
    }

    @Override
    public T visit(ErrorNode node) {
        return parseTreeBuilder.errorNode(node.getLeftExtent(), node.getRightExtent());
    }

    private T convertBasicAndLayout(BodyGrammarSlot slot, NonPackedNode child, int leftExtent, int rightExtent) {
        int bodySize = slot.getRule().getBody().size();
        if (ignoreLayout) { // Layout is insert between symbols in the body of a rule
            bodySize /= 2 + 1;
        }
        List<T> children = new ArrayList<>(bodySize);

        addChildren(child.accept(this), children);

        reverse(children);
        return parseTreeBuilder.nonterminalNode(slot.getRule(), children, leftExtent, rightExtent);
    }

    private static <T> void addChildren(T result, List<T> children) {
        if (result instanceof List<?>) {
            children.addAll((List<T>) result);
        } else {
            if (result != null) {
                children.add(result);
            }
        }
    }

    private T convertStar(NonPackedNode node, Star symbol, int leftExtent, int rightExtent) {
        if (node.isAmbiguous()) {
            handleAmbiguousNode(node);
        }
        List<T> children;
        if (node instanceof EmptyTerminalNode) { // Empty star
            children = emptyList();
        } else {
            Plus plus = (Plus) ((NonterminalNode) node).getRule().getDefinition();
            children = flattenChildrenUnderPlus((NonPackedNode) node.getChildAt(0), plus);
            return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
        }
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertPlus(NonPackedNode leftChild, Plus symbol, int leftExtent, int rightExtent) {
        List<T> children = flattenChildrenUnderPlus(leftChild, symbol);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private List<T> flattenChildrenUnderPlus(NonPackedNode leftChild, Plus symbol) {
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
                T result = node.accept(this);
                if (result != null) {
                    children.add(result);
                }
                break;
            }
        }

        reverse(children);
        return children;
    }

    private T convertSeq(NonPackedNode node, Group symbol, int leftExtent, int rightExtent) {
        List<T> children = new ArrayList<>();
        addChildren(node.accept(this), children);
        reverse(children);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertStart(NonPackedNode node, Start symbol, int leftExtent, int rightExtent) {
        // Layout is inserted before and after the start symbol
        int length = ignoreLayout ? 3 : 1;
        List<T> children = new ArrayList<>(length);
        addChildren(node.accept(this), children);
        reverse(children);
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private T convertAltOpt(NonPackedNode node, Symbol symbol, int leftExtent, int rightExtent) {
        List<T> children;
        T result = node.accept(this);
        if (result == null) {
            children = emptyList();
        } else {
            children = new ArrayList<>(1);
            children.add(result);
        }
        return parseTreeBuilder.metaSymbolNode(symbol, children, leftExtent, rightExtent);
    }

    private NonterminalNode convertUnderPlus(Plus plus, IntermediateNode node, List<T> children) {
        if (node.isAmbiguous()) {
            handleAmbiguousNode(node);
        }

        NonPackedNode leftChild = (NonPackedNode) node.getChildAt(0);
        NonPackedNode rightChild = (NonPackedNode) node.getChildAt(1);

        T result = rightChild.accept(this);
        if (result != null) {
            children.add(result);
        }

        if (leftChild instanceof IntermediateNode) {
            return convertUnderPlus(plus, (IntermediateNode) leftChild, children);
        } else {
            if (leftChild instanceof NonterminalNode) {
                RuntimeRule rule = ((NonterminalNode) leftChild).getRule();
                if (rule.getDefinition() != null && plus.getName().equals(rule.getDefinition().getName())) {
                    return (NonterminalNode) leftChild;
                }
            }
            result = leftChild.accept(this);
            if (result != null) {
                children.add(result);
            }
        }
        return null;
    }

    private void handleAmbiguousNode(NonPackedNode node) {
        List<PackedNode> packedNodes = resultOps.getPackedNodes(node);
        for (int i = 0; i < packedNodes.size(); i++) {
            try {
                DotGraph dotGraph = SPPFToDot.getDotGraph(packedNodes.get(i), input);
                dotGraph.generate("/Users/afroozeh/tree" + i + ".pdf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        throw new AmbiguityException(node, input);
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
