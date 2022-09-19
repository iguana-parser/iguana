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
    public MetaSymbolNode metaSymbolNode(Symbol symbol, List<ParseTreeNode> children, int leftExtent, int rightExtent) {
        MetaSymbolNode node;
        if (symbol instanceof Star) {
            node = new StarNode(symbol, children, leftExtent, rightExtent);
        } else if (symbol instanceof Plus) {
            node = new PlusNode(symbol, children, leftExtent, rightExtent);
        } else if (symbol instanceof Group) {
            node = new GroupNode(symbol, children, leftExtent, rightExtent);
        } else if (symbol instanceof Alt) {
            node = new AltNode(symbol, children.get(0), leftExtent, rightExtent);
        } else if (symbol instanceof Opt) {
            node = new OptionNode(symbol, children.isEmpty() ? null : children.get(0), leftExtent, rightExtent);
        } else if (symbol instanceof Start) {
            node = new StartNode(symbol, children, leftExtent, rightExtent);
        } else {
            throw new RuntimeException("Unknown meta symbol type: " + symbol);
        }

        return node;
    }
}
