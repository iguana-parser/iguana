package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.slot.nodecreator.DummyNodeCreator;
import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.slot.test.FalseConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.logging.LoggerWrapper;

/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final LoggerWrapper log = LoggerWrapper.getLogger(NonterminalGrammarSlot.class);
	
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
		GSSNode cu = parser.getCurrentGSSNode();
		SPPFNode cn = parser.getCurrentSPPFNode();
		
		if(!nonterminal.test(lexer.getInput().charAt(ci))) {
			parser.recordParseError(this);
			return null;
		}
		
		if(preConditions.execute(parser, lexer, parser.getCurrentGSSNode(), ci)) {
			return null;
		}
		
		GSSNode gssNode = parser.hasGSSNode(next, nonterminal);
		if(gssNode == null) {
			gssNode = parser.createGSSNode(next, nonterminal);
			parser.createGSSEdge(next, cu, cn, gssNode);
			return nonterminal;
		}
		
		log.trace("GSSNode found: %s",  gssNode);
		return parser.createGSSEdge(next, cu, cn, gssNode);
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
	}

	@Override
	public String getConstructorCode() {
		StringBuilder sb = new StringBuilder();
		sb.append("new NonterminalGrammarSlot(")
		  .append(id + ", ")
		  .append("\"" +  label + "\"" + ", ")
		  .append("null, ")
		  .append("headSlot" + nonterminal.getId() + ", ")
		  .append(preConditions.getConstructorCode() + ", ")
		  .append(popConditions.getConstructorCode() + ", ")
		  .append(nodeCreator.getConstructorCode() + ", ")
		  .append(nodeCreatorFromPop.getConstructorCode() + ")");
		return sb.toString();
	}

}