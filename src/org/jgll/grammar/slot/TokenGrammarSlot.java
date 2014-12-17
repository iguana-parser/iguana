package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import java.io.PrintWriter;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalNode;


/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenGrammarSlot extends BodyGrammarSlot {
	
	protected final TerminalGrammarSlot slot;
	
	public TokenGrammarSlot(int id, TokenGrammarSlot slot) {
		this(slot.label, slot.previous, slot.slot, slot.preConditions, slot.postConditions, slot.popConditions, slot.nodeCreator, slot.nodeCreatorFromPop);
		this.id = id;
	}
	
	public TokenGrammarSlot(String label, BodyGrammarSlot previous, TerminalGrammarSlot slot,
							Set<Condition> preConditions, Set<Condition> postConditions, Set<Condition> popConditions,
							NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {
		super(label, previous, preConditions, postConditions, popConditions, nodeCreator, nodeCreatorFromPop);
		this.slot = slot;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {

		int ci = parser.getCurrentInputIndex();
		
		if (preConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci))) 
			return null;

		int length = lexer.tokenLengthAt(ci, slot);
		
		if (length < 0) {
			parser.recordParseError(this);
			return null;
		}

		if (postConditions.stream().anyMatch(c -> c.getSlotAction().execute(parser.getInput(), parser.getCurrentGSSNode(), ci + length))) 
			return null;
	
		TerminalNode cr = parser.getSPPFLookup().getTerminalNode(slot, ci, ci + length);
		
		parser.setCurrentInputIndex(ci + length);
		
		NonPackedNode node = nodeCreator.create(parser, next, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		return next;
	}
	
	@Override
	public boolean isNullable() {
		return slot.getRegularExpression().isNullable();
	}
	
	@Override
	public RegularExpression getSymbol() {
		return slot.getRegularExpression();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new TokenGrammarSlot(")
		  .append("\"" +  escape(label) + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + registry.getId(previous)) + ", ")
		  .append(slot.getConstructorCode(registry) + ", ")
		  .append(getConstructorCode(preConditions, registry) + ", ")
		  .append(getConstructorCode(postConditions, registry) + ", ")
		  .append(getConstructorCode(popConditions, registry) + ", ")
		  .append(nodeCreator.getConstructorCode(registry) + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode(registry) + ")")
		  .append(".withId(").append(registry.getId(this)).append(")");
		return sb.toString();
	}

	@Override
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		writer.println("// " + escape(label));
		writer.println("private final int slot" + registry.getId(this) + "() {");
		writer.println("  if (slot" + registry.getId(this) + ".getPreConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, cu, ci))) return L0;");
		writer.println("  int length = lexer.tokenLengthAt(ci, slot" + registry.getId(slot) + ");");
		writer.println("  if (length < 0) {");
		writer.println("    recordParseError(slot" + registry.getId(this) + ");");
		writer.println("    return L0;");
		writer.println("  }");
		writer.println("  if (slot" + registry.getId(this) + ".getPostConditions().stream().anyMatch(c -> c.getSlotAction().execute(input, cu, ci + length))) return L0;");
		writer.println("  NonPackedNode cr = sppfLookup.getTerminalNode(slot" + registry.getId(slot) + ", ci, ci + length);");
		writer.println("  ci = ci + length;");
		writer.println("  cn = slot" + registry.getId(this) + ".getNodeCreator().create(this, slot" + registry.getId(next) + ", cn, cr);");
		writer.println("  return L0;");
		writer.println("}");
	}

	
	@Override
	public TokenGrammarSlot withId(int id) {
		return new TokenGrammarSlot(id, this);
	}
}
