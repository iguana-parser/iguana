package org.jgll.grammar;

import java.util.Set;

/**
 * A grammar slot immediately before a nonterminal.
 * 
 * @author Ali Afroozeh <afroozeh@gmail.com>
 *
 */
public class NonterminalGrammarSlot extends GrammarSlot {

	private final Nonterminal nonterminal;
	private final Set<Terminal> testSet;

	public NonterminalGrammarSlot(int id, String label, int position, GrammarSlot previous, Nonterminal nonterminal, Set<Terminal> testSet) {
		super(id, label, position, previous);
		this.nonterminal = nonterminal;
		this.testSet = testSet;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	@Override
	public String code() {
		String s = "";
		
		if(previous == null) {
			s += "   cu = create(" + next.id + ", cu, ci, cn);\n";
			s += "   label = " + nonterminal.getId() + "; \n}\n";
			s += "// " + next.getName() + "\n";
			s += "private void parse_" + next.id + "() {\n";
			
			GrammarSlot slot = next;
			while(slot.next != null) {
				s += slot.code();
				slot = slot.next;
			}
			s += "   pop(cu, ci, cn);\n";
			s += "   label = L0;\n}\n";
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
			s += "   cu = create(" + next.id + ", cu, ci, cn);\n";
			s += "   label = " + nonterminal.getId() + ";\n}\n";
			s += "// " + next.getName() + "\n";
			s += "private void parse_" + next.id + "(){\n";
		}
		return s;
	}
	
	@Override
	public Set<Integer> getTestSet() {
		return null;
//		return testSet;
	}

}
