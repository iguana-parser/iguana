package org.jgll.grammar.slot;

import static org.jgll.util.generator.GeneratorUtil.*;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TokenSymbolNode;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenGrammarSlot extends BodyGrammarSlot {
	
	protected final int tokenID;
	
	private final RegularExpression regularExpression;
	
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
		  .append("\"" +  label + "\"" + ", ")
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
	public void code(StringBuilder sb) {
		sb.append("// " + label).append(NL)
		  .append("case " + id + ":").append(NL)
		  .append(TAB).append("int length = lexer.tokenLengthAt(ci, " + tokenID + ");").append(NL)
		  .append(TAB).append("if (length < 0) {").append(NL)
		  .append(TAB).append(TAB).append("recordParseError(slot" + id + ");").append(NL)
		  .append(TAB).append(TAB).append("cs = L0;").append(NL)
		  .append(TAB).append(TAB).append("break;").append(NL)
		  .append(TAB).append("}").append(NL)
		  .append(TAB).append("TokenSymbolNode cr = getTokenNode(" + tokenID + ", ci, length);").append(NL)
		  .append(TAB).append("cn = slot" + id + ".getNodeCreator().create(this, slot" + next.getId() + ", cn, cr);").append(NL)
		  .append(NL);
	}

}
