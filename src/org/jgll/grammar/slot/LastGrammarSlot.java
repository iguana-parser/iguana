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
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	protected HeadGrammarSlot head;
	
	protected Object object;
	
	public LastGrammarSlot(String label, BodyGrammarSlot previous, HeadGrammarSlot head, 
						   Set<Condition> popConditions, NodeCreator nodeCreatorFromPop) {
		super(label, previous, new HashSet<>(), new HashSet<>(), 
			  popConditions, DummyNodeCreator.getInstance(), nodeCreatorFromPop);
		this.head = head;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		if (head.testFollowSet(lexer.charAt(parser.getCurrentInputIndex()))) {
			return parser.pop();			
		}
		return null;
	}
	
	@Override
	public boolean isNullable() {
		return false;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}

	@Override
	public Symbol getSymbol() {
		throw new UnsupportedOperationException();
	}
	
	public Object getObject() {
		return object;
	}

	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  if (slot" + registry.getId(head) + ".testFollowSet(lexer.getInput().charAt(ci))) {");
		writer.println("    GrammarSlot returnSlot = pop();");
		writer.println("    if (returnSlot != null) {");
		writer.println("       return returnSlot.getId();");
		writer.println("    }");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new LastGrammarSlot(")
		  .append(registry.getId(this) + ", ")
		  .append("\"" + escape(label) + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + registry.getId(previous) + ", "))
		  .append("slot" + registry.getId(head) + ", ")
		  .append(getConstructorCode(popConditions, registry) + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode(registry) + ")");
		return sb.toString();
	}

}
