package org.iguana.parsetree;

import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFParseTreeVisitor;

import java.util.List;

public class SPPFToParseTree {

    public static <T> T toParseTree(NonterminalNode root, ParseTreeBuilder<T> parseTreeBuilder) {
        SPPFParseTreeVisitor sppfVisitor = new SPPFParseTreeVisitor(parseTreeBuilder);
        VisitResult result = root.accept(sppfVisitor);
        return (T) ((VisitResult.Single) result).getValue();
    }
}
