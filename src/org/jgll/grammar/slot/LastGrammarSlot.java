package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

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

	@Override
	public void code(StringBuilder sb) {
		sb.append("// " + label).append(NL)
		  .append("case " + id + ":").append(NL)
		  .append(TAB).append("if (slot" + head.getId() + ".testFollowSet(lexer.getInput().charAt(ci))) {").append(NL)
		  .append(TAB).append(TAB).append("cs = pop().getId();").append(NL)
		  .append(TAB).append(TAB).append("break;").append(NL)
		  .append(TAB).append("}").append(NL)
		  .append(TAB).append(TAB).append("cs = L0;").append(NL)
		  .append(TAB).append(TAB).append("break;").append(NL)
		  .append(NL);
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
