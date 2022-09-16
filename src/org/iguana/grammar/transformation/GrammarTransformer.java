package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.symbol.Start;

public class GrammarTransformer {

    public static RuntimeGrammar transform(RuntimeGrammar runtimeGrammar) {
        DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
        desugarAlignAndOffside.doAlign();
        RuntimeGrammar grammar = desugarAlignAndOffside.transform(runtimeGrammar);
        grammar = new EBNFToBNF().transform(grammar);
        desugarAlignAndOffside.doOffside();
        grammar = desugarAlignAndOffside.transform(grammar);
        Start startSymbol = runtimeGrammar.getStartSymbol();
        if (startSymbol != null) {
            grammar = new DesugarStartSymbol(startSymbol.getStartSymbol()).transform(grammar);
        }
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);
        grammar = new DesugarState().transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        return grammar;
    }
}


