package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
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
		super(id, label, previous, preConditions, null, popConditions, null, nodeCreatorFromPop);
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
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
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
	public void codeParser(Writer writer) throws IOException {
		
		if(previous == null) {
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "() {\n");
			
			BodyGrammarSlot slot = next;
			while(slot != null) {
				slot.codeParser(writer);
				slot = slot.next;
			}
		} 
		
		else { 
		
			// TODO: add the testSet check
			// code(A ::= α · Xl β) = 
			//						if(test(I[cI ], A, Xβ) {
			// 							cU :=create(RXl,cU,cI,cN); 
			//							gotoLX 
			//						}
			// 						else goto L0
			// RXl:
			codeIfTestSetCheck(writer);
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n");
			codeElseTestSetCheck(writer);
			writer.append("}\n");
			
			writer.append("// " + next + "\n");
			writer.append("private void parse_" + next.id + "(){\n");
		}
	}
	
	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (");
//		int i = 0;
//		for(Terminal terminal : testSet) {
//			writer.append(terminal.getMatchCode());
//			if(++i < testSet.size()) {
//				writer.append(" || ");
//			}
//		}
		writer.append(") {\n");
	}
			
	@Override
	public boolean isNullable() {
		return nonterminal.isNullable();
	}

	@Override
	public Symbol getSymbol() {
		return nonterminal.getNonterminal();
	}

}