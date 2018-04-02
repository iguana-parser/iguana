package org.iguana.gss;

import iguana.utils.input.Input;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.parser.ParserRuntime;
import org.iguana.result.ParserResultOps;
import org.iguana.result.ResultOps;
import org.iguana.sppf.NonPackedNode;
import org.junit.Test;

public class GSSNodeTest {

    @Test
    public void test() {
        NonterminalGrammarSlot<NonPackedNode> slot = new NonterminalGrammarSlot<>(Nonterminal.withName("A"));
        GSSNode<NonPackedNode> gssNode = new GSSNode<>(slot, 0);

        Input input = Input.fromString("Test");
    }
}
