package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
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
	
	public void execute() {
		if(previous == null) {
			
		} else {
			
		}
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
		if(previous == null) {
			writer.append("   cu = create(" + next.id + ", cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + "; \n}\n");
			writer.append("// " + next.getName() + "\n");
			writer.append("private void parse_" + next.id + "() {\n");
			
			GrammarSlot slot = next;
			while(slot.next != null) {
				slot.code(writer);
				slot = slot.next;
			}
			writer.append("   pop(cu, ci, cn);\n");
			writer.append("   label = L0;\n}\n");
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
			writer.append("   cu = create(" + next.id + ", cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n}\n");
			writer.append("// " + next.getName() + "\n");
			writer.append("private void parse_" + next.id + "(){\n");
		}
	}
	
	@Override
	public Set<Integer> getTestSet() {
		return null;
//		return testSet;
	}

}
