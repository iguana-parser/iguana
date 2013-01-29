package org.jgll.grammar;

import java.util.Set;

/**
 * A grammar slot whose next immediate symbol is a terminal.
 * 
 * @author Ali Afroozeh	<afroozeh@gmail.com>
 *
 */
public class TerminalGrammarSlot extends GrammarSlot {
	
	private final Terminal terminal;

	public TerminalGrammarSlot(int id, String label, int position, GrammarSlot previous, Terminal terminal) {
		super(id, label, position, previous);
		this.terminal = terminal;
	}
	
	public Terminal getTerminal() {
		return terminal;
	}

	@Override
	public Set<Integer> getTestSet() {
		return null;
	}
	
	@Override
	public String code() {
		
		String s = "";
		
		// code(A ::= ε) = 
		// 					cR := getNodeT(ε,cI); 
		// 					cN := getNodeP(A ::= ·,cN,cR)
		// 					pop(cU , cI , cN ); 
		// 					goto L0
		if(previous == null && next == null) {
			s += "   cr = getNodeT(-1, ci, ci);\n";
			s += "   cn = getNodeP(" + id + ", cn, cr);\n";
			s += "   pop(cu, ci, cn);\n";
			s += "   label = L0;\n}\n";
		}
		
		// code(A::= x1) = 
		//				  cR := getNodeT(x1,cI); 
		//				  cI :=cI +1 
		// 				  cN := getNodeP(X ::= x1., cN , cR)
		//		 		  pop(cU,cI,cN); 
		//				  gotoL0
		else if(previous == null && next.next == null) {
			s += checkInput(terminal);
			s += "   cr = getNodeT(" + terminal.id + ", ci, ci + 1);\n";
			s += elseCheckInput();
			s += "   ci = ci + 1;\n";
			s += "   cn = getNodeP(" + next.id + ", cn, cr);\n";
			s += "   pop(cu, ci, cn);\n";
			s += "   label = L0;\n}\n";
		}
		
		// If f ≥ 2 and x1 is a terminal
		else if(previous == null && !(next.next == null)) {
			s += checkInput(terminal);
			s += "   cn = getNodeT(" + terminal.id + ", ci, ci + 1);\n";
			s += elseCheckInput();
			s += "   ci = ci + 1;\n";
			
			GrammarSlot slot = next;
			// while slot is one before the end, i.e, α . x
			while(slot.next != null) {
				s += slot.code();
				slot = slot.next;
			}
			s += "   pop(cu, ci, cn);\n";
			s += "   label = L0;\n}\n";
		}
		
		// code(A::=α·aβ) = 
		//					if(I[cI] = a)
		//						cR := getNodeT(a,cI) 
		//					else gotoL0
		// 					cI :=cI +1; 
		//					cN :=getNodeP(A::=αa·β,cN,cR)
		else {
			s += checkInput(terminal);
			s += "     cr = getNodeT(" + terminal.id + ", ci, ci + 1);\n";
			s += "   } else {\n";
			s += "     label = L0; return;\n";
			s += "   }\n";
			
			s += "   ci = ci + 1;\n";
			s += "   cn = getNodeP(" + next.getId() + ", cn, cr);\n";
		}
		
		return s;
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
	
	private String elseCheckInput() {
		return "    } else {label = L0; return; }\n";
	}
}
