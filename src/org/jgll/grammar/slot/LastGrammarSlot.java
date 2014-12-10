package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;

import static org.jgll.util.generator.GeneratorUtil.*;

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
						   ConditionTest popConditions, NodeCreator nodeCreatorFromPop) {
		super(label, previous, FalseConditionTest.getInstance(), FalseConditionTest.getInstance(), 
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
	public void code(PrintWriter writer) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + id + "() {");
		writer.println("  if (slot" + head.getId() + ".testFollowSet(lexer.getInput().charAt(ci))) {");
		writer.println("    GrammarSlot returnSlot = pop();");
		writer.println("    if (returnSlot != null) {");
		writer.println("       return returnSlot.getId();");
		writer.println("    }");
		writer.println("  }");
		writer.println("  return L0;");
		writer.println("}");
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new LastGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" + escape(label) + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + previous.getId()) + ", ")
		  .append("slot" + head.getId() + ", ")
		  .append(popConditions.getConstructorCode() + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode() + ")");
		return sb.toString();
	}

}
