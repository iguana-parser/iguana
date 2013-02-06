package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import org.jgll.parser.GrammarInterpreter;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	private static final long serialVersionUID = 1L;
	private final Set<Terminal> testSet;
	
	public EpsilonGrammarSlot(Rule rule, int id, int position, BodyGrammarSlot previous, Set<Terminal> testSet) {
		super(rule, id, position, previous);
		this.testSet = testSet;
	}
	
	@Override
	public void execute(GrammarInterpreter parser) {
		// A ::= ε
		parser.setCR(parser.getNodeT(TerminalSymbolNode.EPSILON, parser.getCurrentInpuIndex()));
		parser.setCN(parser.getNodeP(this));
		parser.pop();
	}
	
	@Override
	public void code(Writer writer) throws IOException {
		// code(A ::= ε) = 
		// 					cR := getNodeT(ε,cI); 
		// 					cN := getNodeP(A ::= ·,cN,cR)
		// 					pop(cU , cI , cN ); 
		// 					goto L0
		writer.append("   cr = getNodeT(-2, ci);\n");
		writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n");
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
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
		for(Terminal t : testSet) {
			if(t.match(i)) {
				return true;
			}
		}
		return false;
	}

}
