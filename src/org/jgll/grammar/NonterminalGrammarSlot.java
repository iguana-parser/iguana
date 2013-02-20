package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;

/**
 * A grammar slot immediately before a nonterminal.
 * 
 * @author Ali Afroozeh
 *
 */
public class NonterminalGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;

	private Nonterminal nonterminal;
	
	private final Set<Terminal> testSet;
	
	public NonterminalGrammarSlot(Rule rule, int id, int position, BodyGrammarSlot previous, Nonterminal nonterminal, Set<Terminal> testSet) {
		super(rule, id, position, previous);
		if(nonterminal == null) {
			throw new IllegalArgumentException("Nonterminal cannot be null.");
		}
		if(testSet == null) {
			throw new IllegalArgumentException("TestSet cannot be null.");
		}
		this.nonterminal = nonterminal;
		this.testSet = testSet;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	public void setNonterminal(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		if(checkAgainstTestSet(parser.getCurrentInputValue())) {
			parser.setCU(parser.create(next));
			nonterminal.execute(parser);
		} else {
			parser.newParseError(this, parser.getCurrentInpuIndex());
		}
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		
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
		int i = 0;
		for(Terminal terminal : testSet) {
			writer.append(terminal.getMatchCode());
			if(++i < testSet.size()) {
				writer.append(" || ");
			}
		}
		writer.append(") {\n");
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		if(testSet.isEmpty()) {
			return true;
		}
		for(Terminal t : testSet) {
			if(t.match(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterable<Terminal> getTestSet() {
		return testSet;
	}
	
	@Override
	public String getName() {
		return "." + nonterminal.getName();
	}
	
}