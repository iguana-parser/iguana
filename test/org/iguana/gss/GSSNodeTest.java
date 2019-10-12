package org.iguana.gss;

import iguana.regex.Seq;
import iguana.utils.input.Input;
import org.iguana.grammar.RuntimeGrammar;
import org.iguana.grammar.GrammarGraph;
import org.iguana.grammar.GrammarGraphBuilder;
import org.iguana.grammar.slot.EndGrammarSlot;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.RuntimeRule;
import org.iguana.grammar.symbol.Terminal;
import org.iguana.parser.IguanaRuntime;
import org.iguana.result.ParserResultOps;
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
        RuntimeRule rule = RuntimeRule.withHead(Nonterminal.withName("A")).addSymbol(Terminal.from(Seq.from("a"))).build();
        RuntimeGrammar grammar = RuntimeGrammar.builder().addRule(rule).build();

        grammarGraph = GrammarGraphBuilder.from(grammar);
        input = Input.fromString("Test");
    }

    @Test
    public void test() {
        GSSNode gssNode = new DefaultGSSNode(null, 0);

        // TODO: find another way to write these tests
        EndGrammarSlot endGrammarSlot = null; // grammarGraph.getEndGrammarSlot("A ::= a .");

        TerminalNode terminalNode01 = null; //new DefaultTerminalNode("a", 0, 1);
        TerminalNode terminalNode02 = null; //sppfFactory.createTerminalNode("a", 0, 2);
        TerminalNode terminalNode03 = null; //sppfFactory.createTerminalNode("a", 0, 3);

        IguanaRuntime<NonPackedNode> runtime = new IguanaRuntime<>(Configuration.load(), new ParserResultOps());

        // Pop ("a", 0, 1)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode01, runtime));

        NonterminalNode expected = null; //sppfFactory.createNonterminalNode("A ::= a .", terminalNode01);
        Iterator<NonPackedNode> it = gssNode.getPoppedElements().iterator();
        assertEquals(expected, it.next());

        // Pop ("a", 0, 1)
        assertFalse(gssNode.pop(input, endGrammarSlot, terminalNode01, runtime));
        assertEquals(1, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        assertEquals(2, (it.next()).childrenCount());

        // Pop ("a", 0, 2)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode02, runtime));
        assertEquals(2, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        assertEquals(2, it.next().childrenCount());
        assertEquals(1, it.next().childrenCount());

        // Pop ("a", 0, 3)
        assertTrue(gssNode.pop(input, endGrammarSlot, terminalNode03, runtime));
        assertEquals(3, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        assertEquals(2, it.next().childrenCount());
        assertEquals(1, it.next().childrenCount());
        assertEquals(1, it.next().childrenCount());

        // Pop ("a", 0, 2)
        assertFalse(gssNode.pop(input, endGrammarSlot, terminalNode02, runtime));
        assertEquals(3, gssNode.countPoppedElements());
        it = gssNode.getPoppedElements().iterator();
        assertEquals(2, it.next().childrenCount());
        assertEquals(2, it.next().childrenCount());
        assertEquals(1, it.next().childrenCount());
    }
}
