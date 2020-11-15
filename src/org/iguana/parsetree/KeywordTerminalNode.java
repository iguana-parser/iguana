package org.iguana.parsetree;

import org.iguana.grammar.symbol.Terminal;

public class KeywordTerminalNode extends TerminalNode {

    public KeywordTerminalNode(Terminal terminal, int start, int end) {
        super(terminal, start, end);
    }

    @Override
    public String getText() {
        return getTerminal().getName();
    }
}
