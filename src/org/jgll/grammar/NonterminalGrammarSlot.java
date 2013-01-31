package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;

/**
 * A grammar slot immediately before a nonterminal.
 * 
 * @author Ali Afroozeh <afroozeh@gmail.com>
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private final Nonterminal nonterminal;
	private final Set<Terminal> testSet;

	public NonterminalGrammarSlot(int id, String label, int position, BodyGrammarSlot previous, Nonterminal nonterminal, Set<Terminal> testSet) {
		super(id, label, position, previous);
		this.nonterminal = nonterminal;
		this.testSet = testSet;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		parser.setCU(parser.create(next));
		nonterminal.execute(parser);
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
		if(previous == null) {
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + "; \n}\n");
			writer.append("// " + next.getName() + "\n");
			writer.append("private void parse_" + next.id + "() {\n");
			
			BodyGrammarSlot slot = next;
			while(slot != null) {
				slot.code(writer);
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
			writer.append("   cu = create(grammar.getGrammarSlot(" + next.id + "), cu, ci, cn);\n");
			writer.append("   label = " + nonterminal.getId() + ";\n}\n");
			writer.append("// " + next.getName() + "\n");
			writer.append("private void parse_" + next.id + "(){\n");
		}
	}
	
	@Override
	public Set<Terminal> getTestSet() {
		return null;
//		return testSet;
	}

}
