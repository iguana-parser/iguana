package org.iguana.parsetree;

import org.iguana.grammar.runtime.RuntimeRule;
import org.iguana.grammar.slot.TerminalNodeType;
import org.iguana.grammar.symbol.*;
import org.iguana.utils.input.Input;

import java.util.List;
import java.util.Set;

import static org.iguana.parsetree.MetaSymbolNode.*;

public class DefaultParseTreeBuilder implements ParseTreeBuilder<ParseTreeNode> {

    private final Input input;

    public DefaultParseTreeBuilder(Input input) {
        this.input = input;
    }

    @Override
    public TerminalNode terminalNode(Terminal terminal, int leftExtent, int rightExtent) {
        if (terminal == Terminal.epsilon()) return null;
        if (terminal.getNodeType() == TerminalNodeType.Literal) {
            return new KeywordTerminalNode(terminal, leftExtent, rightExtent);
        }
        return new DefaultTerminalNode(terminal, leftExtent, rightExtent, input);
    }

    @Override
    public NonterminalNode nonterminalNode(RuntimeRule rule, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new NonterminalNode(rule, children, leftExtent, rightExtent);
    }

    @Override
    public AmbiguityNode ambiguityNode(Set<ParseTreeNode> ambiguities) {
        return new AmbiguityNode(ambiguities);
    }

    @Override
    public ParseTreeNode starNode(Star symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new StarNode(symbol, children, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode plusNode(Plus symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new PlusNode(symbol, children, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode optNode(Opt symbol, ParseTreeNode child, int leftExtent, int rightExtent) {
        return new OptionNode(symbol,child, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode altNode(Alt symbol, ParseTreeNode child, int leftExtent, int rightExtent) {
        return new AltNode(symbol, child, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode groupNode(Group symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new GroupNode(symbol, children, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode startNode(Start symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        return new StartNode(symbol, children, leftExtent, rightExtent);
    }

    @Override
    public ParseTreeNode errorNode(int leftExtent, int rightExtent) {
        return new ErrorNode(leftExtent, rightExtent, input);
    }
}
