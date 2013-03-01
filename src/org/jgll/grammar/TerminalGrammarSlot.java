package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class TerminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	private final Terminal terminal;

	public TerminalGrammarSlot(int id, int position, BodyGrammarSlot previous, Terminal terminal) {
		super(id, position, previous);
		this.terminal = terminal;
	}
		
	@Override
	public void execute(GrammarInterpreter parser) {
				
		// A::= x1
		if(previous == null && next.next == null) {
			if(terminal.match(parser.getCurrentInputValue())) {
				parser.setCR(parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex()));
				parser.moveInputPointer();
				parser.setCN(parser.getNodeP(next));
				parser.pop();
			} else {
				parser.newParseError(this, parser.getCurrentInpuIndex());
			}

		}
		
		// A ::= x1...xf, f ≥ 2
		else if(previous == null && !(next.next == null)) {
			if(terminal.match(parser.getCurrentInputValue())) {
				parser.setCN(parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex()));
				parser.moveInputPointer();
				next.execute(parser);
			} else {
				parser.newParseError(this, parser.getCurrentInpuIndex());
			}
		}
		
		// A ::= α · a β
		else {
			if(terminal.match(parser.getCurrentInputValue())) {
				parser.setCR(parser.getNodeT(parser.getCurrentInputValue(), parser.getCurrentInpuIndex()));
				parser.moveInputPointer();
				parser.setCN(parser.getNodeP(next));
				next.execute(parser);
			} else {
				parser.newParseError(this, parser.getCurrentInpuIndex());
			}
		}
		
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
		// code(A::= x1) = 
		//				  cR := getNodeT(x1,cI); 
		//				  cI :=cI +1 
		// 				  cN := getNodeP(X ::= x1., cN , cR)
		//		 		  pop(cU,cI,cN); 
		//				  gotoL0
		if(previous == null && next.next == null) {
			writer.append(checkInput(terminal));
			writer.append("   cr = getNodeT(I[ci], ci);\n");
			codeElseTestSetCheck(writer);
			writer.append("   ci = ci + 1;\n");
			writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + next.id + "), cn, cr);\n");
			writer.append("   pop(cu, ci, cn);\n");
			writer.append("   label = L0;\n}\n");
		}
		
		// A ::= x1...xf, f ≥ 2
		else if(previous == null && !(next.next == null)) {
			writer.append(checkInput(terminal));
			writer.append("   cn = getNodeT(I[ci], ci);\n");
			codeElseTestSetCheck(writer);
			writer.append("   ci = ci + 1;\n");
			
			BodyGrammarSlot slot = next;
			// while slot is one before the end, i.e, α . x
			while(slot != null) {
				slot.code(writer);
				slot = slot.next;
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
	public boolean checkAgainstTestSet(int i) {
		return terminal.match(i);
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (").append(terminal.getMatchCode()).append(") {\n");
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		Set<Terminal> set = new HashSet<>();
		set.add(terminal);
		return set;
	}

	@Override
	public String getName() {
		return terminal.toString();
	}	

}
