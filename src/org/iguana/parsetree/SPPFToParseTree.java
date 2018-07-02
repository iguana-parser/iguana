package org.iguana.parsetree;

import org.iguana.grammar.symbol.Symbol;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFParseTreeVisitor;

import java.util.List;
import java.util.Set;

public class SPPFToParseTree {

    public static <T> T toParseTree(NonterminalNode root, Set<Symbol> ignoreSet) {
        SPPFParseTreeVisitor<T> sppfVisitor = new SPPFParseTreeVisitor(new DefaultParseTreeBuilder(), ignoreSet);
        VisitResult result = root.accept(sppfVisitor);
        return (T) ((VisitResult.Single) result).getValue();
    }

    public static <T> T toParseTree(NonterminalNode root, ParseTreeBuilder<T> parseTreeBuilder) {
        SPPFParseTreeVisitor<T> sppfVisitor = new SPPFParseTreeVisitor<>(parseTreeBuilder);
        VisitResult result = root.accept(sppfVisitor);
        return (T) ((VisitResult.Single) result).getValue();
    }
}
