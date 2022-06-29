// This file has been generated, do not directly edit this file!
package org.iguana.iggy.gen;

import org.iguana.grammar.Grammar;
import org.iguana.parser.IguanaParser;
import org.iguana.parsetree.ParseTreeBuilder;
import org.iguana.parsetree.ParseTreeNode;
import org.iguana.utils.input.Input;

public class IggyParser extends IguanaParser {

    public IggyParser(Grammar grammar) {
        super(grammar);
    }

    @Override
    protected ParseTreeBuilder<ParseTreeNode> getParseTreeBuilder(Input input) {
        return new IggyParseTreeBuilder(input);
    }
}
