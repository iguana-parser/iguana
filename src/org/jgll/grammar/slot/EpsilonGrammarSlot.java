package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.util.HashSet;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;
import org.jgll.util.Input;

/**
 * The grammar slot representing an empty body.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	public EpsilonGrammarSlot(String label, HeadGrammarSlot head) {
		super(label, null, head, new HashSet<>(), DummyNodeCreator.getInstance());
	}
	
	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(head.testFollowSet(input.charAt(ci))) {
			TerminalNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode newNode = parser.getSPPFLookup().getNonterminalNode(this.getHead(), ci, ci);
			parser.getSPPFLookup().addPackedNode(newNode, this, ci, DummyNode.getInstance(), epsilonNode);
			parser.setCurrentSPPFNode(newNode);
			parser.pop();
		}
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new EpsilonGrammarSlot(")
		  .append("\"" + escape(label) + "\"" + ", ")
		  .append("slot" + registry.getId(head) + ")")
		  .append(".withId(").append(registry.getId(this)).append(")");
		return sb.toString();
	}
	
	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  if (slot" + registry.getId(head) + ".testFollowSet(lexer.charAt(ci))) {");
		writer.println("    TerminalNode epsilonNode = sppfLookup.getEpsilonNode(ci);");
		writer.println("    NonterminalNode node = sppfLookup.getNonterminalNode(slot" + registry.getId(head) + ", ci, ci);");
		writer.println("    sppfLookup.addPackedNode(node, slot" + registry.getId(this) + ", ci, DummyNode.getInstance(), epsilonNode);");
		writer.println("    cn = node;");
		writer.println("    pop();");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}
	
	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
	}

}
