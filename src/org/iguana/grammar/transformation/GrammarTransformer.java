package org.iguana.grammar.transformation;

import org.iguana.grammar.runtime.RuntimeGrammar;

public class GrammarTransformer {

    public static RuntimeGrammar transform(RuntimeGrammar runtimeGrammar) {
        DesugarAlignAndOffside desugarAlignAndOffside = new DesugarAlignAndOffside();
        desugarAlignAndOffside.doAlign();
        RuntimeGrammar grammar = desugarAlignAndOffside.transform(runtimeGrammar);
        grammar = new EBNFToBNF().transform(grammar);
        desugarAlignAndOffside.doOffside();
        grammar = desugarAlignAndOffside.transform(grammar);
        grammar = new DesugarStartSymbol(runtimeGrammar.getStartSymbols()).transform(grammar);
        DesugarPrecedenceAndAssociativity precedenceAndAssociativity = new DesugarPrecedenceAndAssociativity();
        precedenceAndAssociativity.setOP2();
        grammar = precedenceAndAssociativity.transform(grammar);
        grammar = new DesugarState().transform(grammar);
        grammar = new LayoutWeaver().transform(grammar);
        return grammar;
    }
}


