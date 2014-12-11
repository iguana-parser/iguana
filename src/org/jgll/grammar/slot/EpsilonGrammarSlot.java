package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.util.HashSet;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalNode;

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
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(head.testFollowSet(lexer.charAt(parser.getCurrentInputIndex()))) {
			TerminalNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode node = parser.getSPPFLookup().getNonterminalNode(this.getHead(), ci, ci);
			parser.getSPPFLookup().addPackedNode(node, this, ci, DummyNode.getInstance(), epsilonNode);
			parser.setCurrentSPPFNode(node);
			return parser.pop();
		}

		return null;
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new EpsilonGrammarSlot(")
		  .append("\"" + escape(label) + "\"" + ", ")
		  .append("slot" + registry.getId(head) + ")");
		return sb.toString();
	}
	
	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  if (slot" + registry.getId(head) + ".testFollowSet(lexer.getInput().charAt(ci))) {");
		writer.println("    TokenSymbolNode epsilonNode = sppfLookup.getEpsilonNode(ci);");
		writer.println("    NonterminalNode node = sppfLookup.getNonterminalNode(slot" + registry.getId(head) + ", ci, ci);");
		writer.println("    sppfLookup.addPackedNode(node, slot" + registry.getId(this) + ", ci, DummyNode.getInstance(), epsilonNode);");
		writer.println("    cn = node;");
		writer.println("    GrammarSlot returnSlot = pop();");
		writer.println("    if (returnSlot != null) {");
		writer.println("      return returnSlot.getId();");
		writer.println("    }");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}
	
	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
	}

}
