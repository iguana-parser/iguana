package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.TerminalSymbolNode;

import static org.jgll.util.generator.GeneratorUtil.*;

/**
 * The grammar slot representing an empty body.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	public EpsilonGrammarSlot(int id, String label, HeadGrammarSlot head) {
		super(label, null, head, FalseConditionTest.getInstance(), DummyNodeCreator.getInstance());
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			TerminalSymbolNode epsilonNode = parser.getSPPFLookup().getEpsilonNode(ci);
			NonterminalNode node = parser.getSPPFLookup().getNonterminalNode(this.getHead(), ci, ci);
			parser.getSPPFLookup().addPackedNode(node, this, ci, DummyNode.getInstance(), epsilonNode);
			parser.setCurrentSPPFNode(node);
			return parser.pop();
		}

		return null;
	}
	
	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new EpsilonGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" + escape(label) + "\"" + ", ")
		  .append("slot" + head.getId() + ")");
		return sb.toString();
	}
	
	@Override
	public void code(PrintWriter writer) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + id + "() {");
		writer.println("  if (slot" + head.getId() + ".testFollowSet(lexer.getInput().charAt(ci))) {");
		writer.println("    TokenSymbolNode epsilonNode = sppfLookup.getEpsilonNode(ci);");
		writer.println("    NonterminalNode node = sppfLookup.getNonterminalNode(slot" + head.getId() + ", ci, ci);");
		writer.println("    sppfLookup.addPackedNode(node, slot" + id + ", ci, DummyNode.getInstance(), epsilonNode);");
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
