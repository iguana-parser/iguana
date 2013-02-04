package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GrammarInterpreter;
import org.jgll.sppf.TerminalSymbolNode;

public class EpsilonGrammarSlot extends LastGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public EpsilonGrammarSlot(Rule rule, int id, int position, BodyGrammarSlot previous) {
		super(rule, id, position, previous);
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

}
