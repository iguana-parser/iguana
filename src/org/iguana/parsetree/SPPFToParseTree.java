package org.iguana.parsetree;

import iguana.utils.input.Input;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.SPPFParseTreeVisitor;
import org.iguana.traversal.SPPFVisitor;

import java.util.List;

public class SPPFToParseTree {

    public static <T> T toParseTree(NonterminalNode root, ParseTreeBuilder<T> parseTreeBuilder) {
        SPPFParseTreeVisitor sppfVisitor = new SPPFParseTreeVisitor(parseTreeBuilder);
        List<Object> result = root.accept(sppfVisitor);
        return (T) result.get(0);
    }
}
