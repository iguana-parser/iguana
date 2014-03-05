package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbol.Rule;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;


/**
 * A grammar slot immediately before a nonterminal.
 *
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	protected HeadGrammarSlot nonterminal;
	
	public NonterminalGrammarSlot(Rule rule, int position, int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(rule, position, slotId, label, previous, head);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		this.nonterminal = nonterminal;
	}
	
	public NonterminalGrammarSlot copy(BodyGrammarSlot previous, String label, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		NonterminalGrammarSlot slot = new NonterminalGrammarSlot(rule, position, slotId, label, previous, nonterminal, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}
	
	public HeadGrammarSlot getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(HeadGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer input) {
		
		if(executePreConditions(parser, input)) {
			return null;
		}

		parser.createGSSNode(next, nonterminal);
		return nonterminal;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		int ci = recognizer.getCi();
		org.jgll.recognizer.GSSNode cu = recognizer.getCu();
		
//		if(test(ci, lexer)) {
			recognizer.update(recognizer.create(next, cu, ci), ci);
			return nonterminal;
//		} else { 
//			recognizer.recognitionError(cu, ci);
//			return null;
//		}
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

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		if(this == slot) {
			return true;
		}
		
		if(!(slot instanceof NonterminalGrammarSlot)) {
			return false;
		}
		
		NonterminalGrammarSlot other = (NonterminalGrammarSlot) slot;
		
		return nonterminal.getNonterminal().equals(other.nonterminal.getNonterminal());
	}
	
}