package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

import static org.jgll.util.generator.GeneratorUtil.*;


/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenGrammarSlot extends BodyGrammarSlot {
	
	protected final int tokenID;
	
	protected final RegularExpression regularExpression;
	
	public TokenGrammarSlot(int id, String label, BodyGrammarSlot previous, RegularExpression regularExpression, int tokenID,
							ConditionTest preConditions, ConditionTest postConditions, ConditionTest popConditions,
							NodeCreator nodeCreator, NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, preConditions, postConditions, popConditions, nodeCreator, nodeCreatorFromPop);
		this.regularExpression = regularExpression;
		this.tokenID = tokenID;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {

		int ci = parser.getCurrentInputIndex();
		
		if(preConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci)) {
			return null;
		}

		int length = lexer.tokenLengthAt(ci, tokenID);
		
		if (length < 0) {
			parser.recordParseError(this);
			return null;
		}
		
		if(postConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci + length)) {
			return null;
		}
		
		TokenSymbolNode cr = parser.getTokenNode(tokenID, ci, length);
		
		SPPFNode node = nodeCreator.create(parser, next, parser.getCurrentSPPFNode(), cr);
		
		parser.setCurrentSPPFNode(node);
		
		return next;
	}
	
	@Override
	public boolean isNullable() {
		return regularExpression.isNullable();
	}
	
	public int getTokenID() {
		return tokenID;
	}
	
	@Override
	public RegularExpression getSymbol() {
		return regularExpression;
	}
	
	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new TokenGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" +  escape(label) + "\"" + ", ")
		  .append((previous == null ? "null" : "slot" + previous.getId()) + ", ")
		  .append(regularExpression.getConstructorCode() + ", ")
		  .append(tokenID + ", ")
		  .append(preConditions.getConstructorCode() + ", ")
		  .append(postConditions.getConstructorCode() + ", ")
		  .append(popConditions.getConstructorCode() + ", ")
		  .append(nodeCreator.getConstructorCode() + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode() + ")");
		return sb.toString();
	}

	@Override
	public void code(PrintWriter writer) {
		writer.println("private final int slot" + id + "() {");
		writer.println("if (preConditions.execute(this, lexer, cu, ci)) return L0;");
		writer.println("  length = lexer.tokenLengthAt(ci, " + tokenID + ");");
		writer.println("  if (length < 0) {");
		writer.println("    recordParseError(slot" + id + ");");
		writer.println("    return L0;");
		writer.println("  }");
		writer.println("if (postConditions.execute(parser, lexer, cu, ci + length)) return L0;");
		writer.println("  cr = getTokenNode(" + tokenID + ", ci, length);");
		writer.println("  cn = slot" + id + ".getNodeCreator().create(this, slot" + next.getId() + ", cn, cr);");
		writer.println("}");
	}

}
