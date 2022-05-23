package org.iguana.gss;

import iguana.utils.input.Input;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.runtime.RuntimeGrammar;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.slot.NonterminalGrammarSlot;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.ParserResultOps;
import org.iguana.sppf.DefaultTerminalNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.sppf.NonterminalNode;
import org.iguana.sppf.TerminalNode;
import org.iguana.util.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class GSSNodeTest {

    private Input input;
    private GrammarGraph grammarGraph;

    @Before
    public void init() {
        RuntimeGrammar grammar = Grammar.fromIggyGrammar(
            "start A = 'a'").toRuntimeGrammar();

        grammarGraph = GrammarGraphBuilder.from(grammar);
        input = Input.fromString("Test");
    }

    @Test
    public void test() {
        GSSNode gssNode = new StartGSSNode<>(grammarGraph.getNonterminalGrammarSlots().get(0), 0);

        NonterminalGrammarSlot nonterminalGrammarSlot = grammarGraph.getNonterminalGrammarSlots().get(0);// A
        EndGrammarSlot endGrammarSlot = (EndGrammarSlot) grammarGraph.getBodyGrammarSlots().get(1); // A ::= a.

        TerminalNode terminalNode01 = new DefaultTerminalNode(grammarGraph.getTerminalGrammarSlots().get(1), 0, 1);
        TerminalNode terminalNode02 = new DefaultTerminalNode(grammarGraph.getTerminalGrammarSlots().get(1), 0, 2);
        TerminalNode terminalNode03 = new DefaultTerminalNode(grammarGraph.getTerminalGrammarSlots().get(1), 0, 3);

        ParserResultOps resultOps = new ParserResultOps();
        IguanaRuntime<NonPackedNode> runtime = new IguanaRuntime<>(Configuration.load(), resultOps);

        // Pop ("a", 0, 1)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode01, runtime));

        Iterator<NonPackedNode> it = gssNode.getPoppedElements().iterator();
        NonterminalNode nonterminalNode1 = (NonterminalNode) it.next(); // (A, 0, 1)
        assertEquals(nonterminalGrammarSlot, nonterminalNode1.getGrammarSlot());
        assertEquals(0, nonterminalNode1.getLeftExtent());
        assertEquals(1, nonterminalNode1.getRightExtent());

        // Pop ("a", 0, 1)
        assertFalse(gssNode.pop(input, endGrammarSlot, terminalNode01, runtime));
        assertEquals(1, gssNode.countPoppedElements());
        assertTrue(nonterminalNode1.isAmbiguous());
        assertEquals(2, resultOps.getPackedNodes(nonterminalNode1).size());

        // Pop ("a", 0, 2)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode02, runtime));
        assertEquals(2, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        it.next(); // (A, 0, 1)
        NonterminalNode nonterminalNode2 = (NonterminalNode) it.next(); // (A, 0, 2)
        assertFalse(nonterminalNode2.isAmbiguous());
        assertEquals(2, resultOps.getPackedNodes(nonterminalNode1).size());

        // Pop ("a", 0, 3)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode03, runtime));
        assertEquals(3, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        it.next(); // (A, 0, 1)
        it.next(); // (A, 0, 2)
        NonterminalNode nonterminalNode3 = (NonterminalNode) it.next();
        assertEquals(2, resultOps.getPackedNodes(nonterminalNode1).size());
        assertFalse(nonterminalNode2.isAmbiguous());
        assertFalse(nonterminalNode3.isAmbiguous());

        // Pop ("a", 0, 2)
        assertFalse(gssNode.pop(input, endGrammarSlot, terminalNode02, runtime));
        assertEquals(3, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        assertTrue(nonterminalNode2.isAmbiguous());
        assertEquals(2, resultOps.getPackedNodes(nonterminalNode1).size());
        assertEquals(2, resultOps.getPackedNodes(nonterminalNode2).size());
    }
}
