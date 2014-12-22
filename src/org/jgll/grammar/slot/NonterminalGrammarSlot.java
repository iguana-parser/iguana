package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
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
	
	private NonterminalGrammarSlot(int id, NonterminalGrammarSlot slot) {
		this(slot.label, slot.previous, slot.nonterminal, slot.preConditions, slot.popConditions, slot.nodeCreatorFromPop);
		this.id = id;
	}
	
	public NonterminalGrammarSlot(String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, 
								  Set<Condition> preConditions, Set<Condition> popConditions, NodeCreator nodeCreatorFromPop) {
		
		super(label, previous, preConditions, new HashSet<>(), popConditions, DummyNodeCreator.getInstance(), nodeCreatorFromPop);
		
		if(nonterminal == null) throw new IllegalArgumentException("Nonterminal cannot be null.");
		
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
		
		if (!nonterminal.test(lexer.charAt(ci))) {
			parser.recordParseError(this);
			return null;
		}
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci))) {
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
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  if (!slot" + registry.getId(nonterminal) + ".test(lexer.charAt(ci))) {");
		writer.println("    recordParseError(slot" + registry.getId(this) + ");");
		writer.println("    return L0;");
		writer.println("  }");
		writer.println("  if (slot" + registry.getId(this) + ".getPreConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, cu, ci))) {");
		writer.println("    return L0;");
		writer.println("  }");
		writer.println("  HeadGrammarSlot returnSlot = create(slot" + registry.getId(next) + ", slot" + registry.getId(nonterminal) + ");");
		writer.println("  if (returnSlot != null) {");
		writer.println("    return returnSlot.getId();");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new NonterminalGrammarSlot(")
		  .append("\"" +  escape(label) + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + registry.getId(previous)) + ", ")
		  .append("slot" + registry.getId(nonterminal) + ", ")
		  .append(getConstructorCode(preConditions, registry) + ", ")
		  .append(getConstructorCode(popConditions, registry) + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode(registry) + ")")
		  .append(".withId(").append(registry.getId(this)).append(")");
		return sb.toString();
	}

	@Override
	public NonterminalGrammarSlot withId(int id) {
		return new NonterminalGrammarSlot(id, this);
	}

}