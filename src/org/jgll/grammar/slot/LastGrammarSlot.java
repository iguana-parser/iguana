package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
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
	
	public LastGrammarSlot(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head, 
						   ConditionTest popConditions, NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, null, null, popConditions, null, nodeCreatorFromPop);
		this.head = head;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		if (head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
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

//	GrammarSlot returnSlot = pop();
//	if (returnSlot != null) {
//	    cs = returnSlot.getId();
//	    break;			
//	}

	
	@Override
	public void code(PrintWriter writer) {
		writer.println("// " + label);
		writer.println("case " + id + ":");
		writer.println("  if (slot" + head.getId() + ".testFollowSet(lexer.getInput().charAt(ci))) {");
		writer.println("    GrammarSlot returnSlot = pop();");
		writer.println("    if (returnSlot != null) {");
		writer.println("       cs = returnSlot.getId();");
		writer.println("       break;");
		writer.println("    }");
		writer.println("  }");
		writer.println("  cs = L0;");
		writer.println("  break;");
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new LastGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" + label + "\"" + ", ")
		  .append("slot" + previous.getId() + ", ")
		  .append("slot" + head.getId() + ", ")
		  .append(popConditions.getConstructorCode() + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode() + ")");
		return sb.toString();
	}

}
