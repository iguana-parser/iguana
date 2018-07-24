package org.iguana.parsetree;


public interface ParseTreeVisitor<T> {
    T visitNonterminalNode(NonterminalNode node);
    T visitAmbiguityNode(AmbiguityNode node);
    T visitTerminalNode(TerminalNode node);
    T visitMetaSymbolNode(MetaSymbolNode node);
}
