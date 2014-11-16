package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;

/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	protected HeadGrammarSlot nonterminal;
	
	public NonterminalGrammarSlot(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, 
								  ConditionTest preConditions, ConditionTest popConditions,
								  NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, preConditions, FalseConditionTest.getInstance(), popConditions, DummyNodeCreator.getInstance(), nodeCreatorFromPop);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.nonterminal = nonterminal;
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if (!nonterminal.test(lexer.getInput().charAt(ci))) {
			parser.recordParseError(this);
			return null;
		}
		
		if (preConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci)) {
			return null;
		}

		return parser.create(next, nonterminal);
	}
	
	@Override
	public boolean isNullable() {
		return nonterminal.isNullable();
	}

	@Override
	public Symbol getSymbol() {
		return nonterminal.getNonterminal();
	}

	@Override
	public void code(PrintWriter writer) {
		writer.println("// " + label);
		writer.println("case " + id + ":");
		writer.println("  if (!slot" + nonterminal.getId() + ".test(lexer.getInput().charAt(ci))) {");
		writer.println("    recordParseError(slot" + id + ");");
		writer.println("    cs = L0;");
		writer.println("  } else if (slot" + id + ".getPreConditions().execute(this, lexer, cu, ci)) {");
		writer.println("    cs = L0;");
		writer.println("  } else {");
		writer.println("    GrammarSlot returnSlot = create(slot" + next.getId() + ", slot" + nonterminal.getId() + ");");
		writer.println("    if (returnSlot != null) {");
		writer.println("      cs = returnSlot.getId();");
		writer.println("    }");
		writer.println("    cs = L0;");
		writer.println("  }");
		writer.println("  break;");
	}
	
	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new NonterminalGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" +  label + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + previous.getId()) + ", ")
		  .append("slot" + nonterminal.getId() + ", ")
		  .append(preConditions.getConstructorCode() + ", ")
		  .append(popConditions.getConstructorCode() + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode() + ")");
		return sb.toString();
	}

}