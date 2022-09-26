package org.iguana.parsetree;

import org.iguana.grammar.symbol.Terminal;

public class KeywordTerminalNode extends TerminalNode {

    public KeywordTerminalNode(Terminal terminal, int start, int end) {
        super(terminal, start, end);
    }

    @Override
    public String getText() {
        // Keyword names start and end with ', so here we have to strip them.
        // TODO: document this somewhere
        String name = getTerminal().getName();
        return name.substring(1, name.length() - 1);
    }

    @Override
    public boolean hasChildren() {
        return false;
    }
}
