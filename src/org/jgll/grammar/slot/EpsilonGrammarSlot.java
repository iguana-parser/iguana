package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbol.Epsilon;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalNode;

/**
 * The grammar slot representing an empty body.
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class EpsilonGrammarSlot extends LastGrammarSlot {

	public EpsilonGrammarSlot(int id, String label, HeadGrammarSlot head) {
		super(id, label, null, head, null, null);
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			// Do not create epsilon nodes
			NonterminalNode node = parser.getSPPFLookup().getNonterminalNode(this.getHead(), ci, ci);
			parser.getSPPFLookup().addPackedNode(node, this, ci, DummyNode.getInstance(), DummyNode.getInstance());
			parser.setCurrentSPPFNode(node);
			return parser.pop();
		}

		return null;
	}
	
	@Override
	public Symbol getSymbol() {
		return Epsilon.getInstance();
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		/**
		 * code(A ::= ε) =
		 * 				  cR ::= getNodeT(ε,cI);
		 * 				  cN ::= getNodeP(A ::= ·,cN,cR)
		 * 				  pop(cU , cI , cN );
		 * 				  goto L0;
		 */
		writer.append("   cr = getNodeT(-2, ci);\n");
		writer.append("   cn = getNodeP(grammar.getGrammarSlot(" + id + "), cn, cr);\n");
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}	
}
