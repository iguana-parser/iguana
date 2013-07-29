package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.LastGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;


/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	protected final Terminal terminal;

	public TerminalGrammarSlot(String label, int position, BodyGrammarSlot previous, Terminal terminal, HeadGrammarSlot head) {
		super(label, position, previous, head);
		this.terminal = terminal;
	}
		
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		int ci = parser.getCurrentInputIndex();
		int charAtCi = input.charAt(ci);

		if(terminal.match(charAtCi)) {
			TerminalSymbolNode cr = parser.getTerminalNode(charAtCi);
			if(next instanceof LastGrammarSlot) {
				parser.getNonterminalNode((LastGrammarSlot) next, cr);
				parser.pop();
				return null;
			} else {
				parser.getIntermediateNode(next, cr);
			}
		} 
		else {
			parser.recordParseError(this);
			return null;
		}
		
		return next;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		int ci = recognizer.getCi();
		org.jgll.recognizer.GSSNode cu = recognizer.getCu();
		int charAtCi = input.charAt(ci);
		
		// A::= x1
		if(previous == null && next.next() == null) {
			if(terminal.match(charAtCi)) {
				ci++;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
		// A ::= x1...xf, f ≥ 2
		else if(previous == null && !(next.next() == null)) {
			if(terminal.match(charAtCi)) {
				ci++;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
		// A ::= α · a β
		else {
			if(terminal.match(charAtCi)) {
				ci++;
				recognizer.update(cu, ci);
			} else {
				recognizer.recognitionError(cu, ci);
				return null;
			}
		}
		
		return next;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		
		// code(A::= x1) = 
		//				  cR := getNodeT(x1,cI); 
		//				  cI :=cI +1 
		// 				  cN := getNodeP(X ::= x1., cN , cR)
		//		 		  pop(cU,cI,cN); 
		//				  gotoL0
		if(previous == null && next.next() == null) {
			writer.append(checkInput(terminal));
			writer.append("   cr = getNodeT(I[ci], ci);\n");
			codeElseTestSetCheck(writer);
			writer.append("   ci = ci + 1;\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.getId() + "), cn, cr);\n");
			writer.append("   pop(cu, ci, cn);\n");
			writer.append("   label = L0;\n}\n");
		}
		
		// A ::= x1...xf, f ≥ 2
		else if(previous == null && !(next.next() == null)) {
			writer.append(checkInput(terminal));
			writer.append("   cn = getNodeT(I[ci], ci);\n");
			codeElseTestSetCheck(writer);
			writer.append("   ci = ci + 1;\n");
			
			BodyGrammarSlot slot = next;
			// while slot is one before the end, i.e, α . x
			while(slot != null) {
				slot.codeParser(writer);
				slot = slot.next();
			}
		}
		
		// code(A ::= α · a β) = 
		//					if(I[cI] = a)
		//						cR := getNodeT(a,cI) 
		//					else gotoL0
		// 					cI :=cI +1; 
		//					cN :=getNodeP(A::=αa·β,cN,cR)
		else {
			writer.append(checkInput(terminal));
			writer.append("     cr = getNodeT(I[ci], ci);\n");
			codeElseTestSetCheck(writer);
			
			writer.append("   ci = ci + 1;\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.getId() + "), cn, cr);\n");
		}
	}
	
	private String checkInput(Terminal terminal) {
		String s = "";
		if(terminal instanceof Range) {
			s += "   if(I[ci] >= " +  ((Range) terminal).getStart() + " + && I[ci] <= " + ((Range) terminal).getEnd() + ") {\n";	
		} else {
			s += "   if(I[ci] == " + ((Character) terminal).get() + ") {\n";
		}
		return s;
	}

	@Override
	public boolean testFirstSet(int index, Input input) {
		return terminal.match(input.charAt(index));
	}
	
	@Override
	public boolean testFollowSet(int index, Input input) {
		return false;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (").append(terminal.getMatchCode()).append(") {\n");
	}

	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public boolean isNullable() {
		return false;
	}


	@Override
	public Symbol getSymbol() {
		return terminal;
	}

}
